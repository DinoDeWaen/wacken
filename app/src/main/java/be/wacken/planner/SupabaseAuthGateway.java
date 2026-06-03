package be.wacken.planner;

import java.io.IOException;

interface SupabaseAuthGateway {
    AuthSession refresh(AuthSession session) throws IOException;
}
