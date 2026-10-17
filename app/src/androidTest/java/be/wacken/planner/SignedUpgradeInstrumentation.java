package be.wacken.planner;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.os.*;
import android.view.*;
import android.widget.TextView;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Opt-in signed APK upgrade probe, separate from the normal JUnit suite.
 * Only the emulator-only upgrade runner invokes it. No real account is used.
 */
public final class SignedUpgradeInstrumentation extends Instrumentation {
    private static final String USER="upgrade-member", GROUP="upgrade-group", BAND="Upgrade Known";
    private Bundle arguments;
    @Override public void onCreate(Bundle arguments) {super.onCreate(arguments);this.arguments=arguments;start();}
    @Override public void onStart() {
        Bundle result=new Bundle();
        try {
            String phase=arguments.getString("upgradePhase","");
            require(phase.equals("seed")||phase.equals("verify"),"Explicit seed or verify phase required");
            Context context=getTargetContext();
            Class<?> type=context.getClassLoader().loadClass("be.wacken.planner.persistence.WackenDatabase");
            RoomDatabase room=(RoomDatabase)type.getMethod("get",Context.class).invoke(null,context);
            SupportSQLiteDatabase db=room.getOpenHelper().getWritableDatabase();
            if(phase.equals("seed"))seed(context,db);else verify(context,db);
            result.putString("upgrade_result","PASS");
            result.putString("upgrade_phase",phase);
            result.putString("package_version",context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionName);
            finish(Activity.RESULT_OK,result);
        } catch(Throwable error) {
            result.putString("upgrade_result","FAIL");
            result.putString("failure",error.getClass().getSimpleName()+": "+error.getMessage());
            finish(Activity.RESULT_CANCELED,result);
        }
    }
    private void seed(Context context,SupportSQLiteDatabase db)throws Exception {
        require(number(db,"PRAGMA user_version")==9,"Seed requires the actual published Room9 installation");
        require(number(db,"SELECT count(*) FROM bands WHERE name LIKE 'Upgrade %'")==0,"Fresh isolated upgrade data required");
        db.beginTransaction();
        try {
            db.execSQL("UPDATE festivals SET status='ARCHIVED'");
            db.execSQL("INSERT INTO festivals(id,name,status) VALUES ('upgrade-archive','Upgrade archived','ARCHIVED'),('upgrade-active','Upgrade active','ACTIVE')");
            for(String name:new String[]{BAND,"Upgrade Active Only","Upgrade Unknown"}) {
                db.execSQL("INSERT INTO bands(name,biography,imageUrl,youtubeUrl,spotifyUrl) VALUES (?,?,NULL,?,?)",
                    new Object[]{name,"Preserved canonical biography","https://youtube.test/upgrade","https://spotify.test/upgrade"});
                db.execSQL("INSERT INTO festival_lineup_entries(festivalId,bandName,uploadedDisplayName) VALUES ('upgrade-active',?,?)",new Object[]{name,name});
            }
            db.execSQL("INSERT INTO festival_lineup_entries(festivalId,bandName,uploadedDisplayName) VALUES ('upgrade-archive',?,?)",new Object[]{BAND,BAND});
            db.execSQL("INSERT INTO festival_planning_ratings(groupId,userName,festivalId,bandName,value,syncStatus) VALUES (?,?, 'upgrade-active',?,5,'PENDING')",new Object[]{GROUP,USER,BAND});
            db.execSQL("INSERT INTO festival_planning_ratings(groupId,userName,festivalId,bandName,value,syncStatus) VALUES (?,'other-member','upgrade-active',?,2,'SYNCED')",new Object[]{GROUP,BAND});
            db.execSQL("INSERT INTO ratings(userName,bandName,value,groupId,syncStatus) VALUES (?,?,5,?,'PENDING')",new Object[]{USER,BAND,GROUP});
            db.execSQL("INSERT INTO real_ratings(userName,bandName,value) VALUES (?,?,3)",new Object[]{USER,BAND});
            event(db,"11111111-1111-4111-8111-111111111111",BAND,"upgrade-archive",4,"2026-08-01T12:00:00Z","SYNCED");
            event(db,"22222222-2222-4222-8222-222222222222",BAND,"upgrade-active",3,"2026-10-01T12:00:00Z","PENDING");
            event(db,"33333333-3333-4333-8333-333333333333","Upgrade Active Only","upgrade-active",5,"2026-10-01T12:00:00Z","SYNCED");
            event(db,"44444444-4444-4444-8444-444444444444","Upgrade Unknown","upgrade-archive",5,"1970-01-01T00:00:00Z","SYNCED");
            db.execSQL("INSERT INTO schedule_locks(groupId,festivalId,conflictKey,selectedCandidateKey,syncStatus,operation) VALUES (?,'upgrade-active','upgrade-conflict','upgrade-candidate','PENDING','UPSERT')",new Object[]{GROUP});
            db.setTransactionSuccessful();
        } finally {db.endTransaction();}
        require(context.getSharedPreferences("supabase-session",0).edit().putString("accessToken","offline-upgrade-fixture")
            .putString("refreshToken","offline-upgrade-fixture").putString("userId",USER)
            .putString("email","upgrade@example.invalid").putLong("expiresAt",Long.MAX_VALUE)
            .putString("groupId",GROUP).putString("groupRole","member").commit(),"Seed auth persisted");
        try(FileOutputStream stream=new FileOutputStream(new File(context.getFilesDir(),"upgrade-sentinel.tsv"))) {
            stream.write("metadata\tretained\n".getBytes(StandardCharsets.UTF_8));
        }
        require(number(db,"SELECT count(*) FROM personal_band_rating_events WHERE userName='upgrade-member'")==4,"All original fixture events seeded");
    }
    private static void event(SupportSQLiteDatabase db,String id,String band,String festival,int value,String time,String state) {
        db.execSQL("INSERT INTO personal_band_rating_events(id,userName,bandName,festivalId,festivalNameSnapshot,value,createdAt,syncStatus) VALUES (?,?,?,?,?,?,?,?)",
            new Object[]{id,USER,band,festival,festival.equals("upgrade-active")?"Upgrade active":"Upgrade archived",value,time,state});
    }
    private void verify(Context context,SupportSQLiteDatabase db)throws Exception {
        require(number(db,"PRAGMA user_version")==12,"Candidate migrated to Room12");
        var preferences=context.getSharedPreferences("supabase-session",0);
        require(USER.equals(preferences.getString("userId",""))&&GROUP.equals(preferences.getString("groupId",""))
            &&"offline-upgrade-fixture".equals(preferences.getString("accessToken",""))
            &&"offline-upgrade-fixture".equals(preferences.getString("refreshToken",""))
            &&preferences.getLong("expiresAt",0)==Long.MAX_VALUE,"Auth/session preserved");
        require(number(db,"SELECT count(*) FROM bands WHERE name LIKE 'Upgrade %' AND biography='Preserved canonical biography' AND youtubeUrl='https://youtube.test/upgrade' AND spotifyUrl='https://spotify.test/upgrade'")==3,"Canonical metadata/links preserved");
        require(number(db,"SELECT count(*) FROM festivals WHERE id='upgrade-active' AND name='Upgrade active' AND status='ACTIVE'")==1,"Active identity retained");
        require(number(db,"SELECT count(*) FROM festivals WHERE id='upgrade-archive' AND status='ARCHIVED'")==1,"Archive retained");
        require(number(db,"SELECT count(*) FROM festival_lineup_entries WHERE festivalId='upgrade-active'")==3,"Canonical lineup retained");
        require(number(db,"SELECT count(*) FROM personal_band_rating_events WHERE userName='upgrade-member'")==4,"Original events retained");
        require(number(db,"SELECT count(*) FROM personal_band_rating_events WHERE id='22222222-2222-4222-8222-222222222222' AND value=3 AND createdAt='2026-10-01T12:00:00Z' AND syncStatus='PENDING'")==1,"Pending history/date retained");
        require(number(db,"SELECT count(*) FROM personal_band_ratings WHERE userName='upgrade-member'")==1,"Active/unknown values never establish an opinion");
        require(number(db,"SELECT count(*) FROM personal_band_ratings WHERE userName='upgrade-member' AND bandName='Upgrade Known' AND value=4 AND sourceFestivalId='upgrade-archive'")==1,"Acknowledged archived source established");
        require(number(db,"SELECT count(*) FROM festival_after_ratings WHERE userName='upgrade-member' AND festivalId='upgrade-active' AND bandName='Upgrade Known' AND value=3 AND requiresReview=1 AND syncStatus='REVIEW'")==1,"Recovered active outcome requires review; no invented promotion");
        require(number(db,"SELECT count(*) FROM festival_planning_ratings WHERE userName='upgrade-member' AND festivalId='upgrade-active' AND bandName='Upgrade Known' AND value=5 AND syncStatus='PENDING'")==1,"Pending planning retained");
        require(number(db,"SELECT count(*) FROM festival_planning_ratings WHERE userName='other-member' AND value=2 AND syncStatus='SYNCED'")==1,"Other owner retained");
        require(number(db,"SELECT count(*) FROM ratings WHERE userName='upgrade-member' AND value=5 AND syncStatus='PENDING'")==1,"Legacy pending retained");
        require(number(db,"SELECT count(*) FROM schedule_locks WHERE groupId='upgrade-group' AND syncStatus='PENDING' AND selectedCandidateKey='upgrade-candidate'")==1,"Pending schedule decision retained");
        try(FileInputStream stream=new FileInputStream(new File(context.getFilesDir(),"upgrade-sentinel.tsv"))) {
            byte[] bytes=new byte[64];int length=stream.read(bytes);
            require("metadata\tretained\n".equals(new String(bytes,0,length,StandardCharsets.UTF_8)),"Private file preserved");
        }
        ActivityMonitor monitor=addMonitor("be.wacken.planner.MainActivity",null,false);
        Activity screen;
        try {
            context.startActivity(new Intent().setClassName(context.getPackageName(),"be.wacken.planner.MainActivity")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            screen=waitForMonitorWithTimeout(monitor,20000);
        } finally {removeMonitor(monitor);}
        require(screen!=null,"Normal app startup reached MainActivity within20 seconds");
        AtomicBoolean visible=new AtomicBoolean();
        long deadline=SystemClock.elapsedRealtime()+15000;
        while(SystemClock.elapsedRealtime()<deadline&&!visible.get()) {
            runOnMainSync(()->visible.set(contains(screen.getWindow().getDecorView(),BAND)));
            if(!visible.get())SystemClock.sleep(25);
        }
        require(visible.get(),"Normal signed app startup renders retained cached lineup offline");
        // Startup/failed offline sync must preserve the same pending operations.
        require(number(db,"SELECT count(*) FROM festival_planning_ratings WHERE userName='upgrade-member' AND syncStatus='PENDING'")==1,"Startup did not discard pending planning");
        require(number(db,"SELECT count(*) FROM personal_band_rating_events WHERE userName='upgrade-member' AND syncStatus='PENDING'")==1,"Startup did not discard pending history");
        runOnMainSync(screen::finish);
    }
    private static long number(SupportSQLiteDatabase db,String query) {
        try(Cursor cursor=db.query(query)){require(cursor.moveToFirst(),"Query returned a row");return cursor.getLong(0);}
    }
    private static boolean contains(View view,String text) {
        if(view instanceof TextView label&&label.getText().toString().contains(text))return true;
        if(view instanceof ViewGroup group)for(int i=0;i<group.getChildCount();i++)if(contains(group.getChildAt(i),text))return true;
        return false;
    }
    private static void require(boolean condition,String message){if(!condition)throw new AssertionError(message);}
}
