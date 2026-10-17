package be.wacken.planner;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;

public final class ExpectedMemberRequestTest {
    @Test public void switchedAccountIsRejectedBeforeAnyRequestIsSent(){
        Store store=new Store(session("other"));
        var request=new SupabaseAuthenticatedRequest(new SupabaseSessionManager(store,s->{throw new AssertionError();}),"test");
        AtomicInteger sent=new AtomicInteger();
        try{request.executeForUser("member",s->{sent.incrementAndGet();return new SupabaseAuthenticatedRequest.Response(200,"ok");});fail();}
        catch(IOException expected){assertTrue(expected.getMessage().contains("changed"));}
        assertEquals(0,sent.get());
    }
    @Test public void accountSwitchDuringExpiredTokenRetryCannotMutateTheOtherMember(){
        Store store=new Store(session("member"));
        var request=new SupabaseAuthenticatedRequest(new SupabaseSessionManager(store,s->{throw new AssertionError();}),"test");
        AtomicInteger sent=new AtomicInteger();
        try{request.executeForUser("member",s->{
            sent.incrementAndGet();store.save(session("other"));
            return new SupabaseAuthenticatedRequest.Response(401,"{\"message\":\"JWT expired\"}");
        });fail();}
        catch(IOException expected){assertTrue(expected.getMessage().contains("changed"));}
        assertEquals(1,sent.get());
    }
    @Test public void unchangedMemberCanConfirmItsRequest()throws Exception{
        Store store=new Store(session("member"));
        var request=new SupabaseAuthenticatedRequest(new SupabaseSessionManager(store,s->{throw new AssertionError();}),"test");
        assertEquals("confirmed",request.executeForUser("member",s->new SupabaseAuthenticatedRequest.Response(200,"confirmed")));
    }
    private AuthSession session(String user){return new AuthSession("token-"+user,"refresh",user,"fixture@example.invalid",Long.MAX_VALUE,"group","member");}
    private static final class Store implements AuthSessionRepository {
        private AuthSession value;
        Store(AuthSession session){value=session;}
        public AuthSession load(){return value;}
        public void save(AuthSession session){value=session;}
        public void clear(){value=new AuthSession("","","","",0,"","");}
    }
}
