package be.wacken.planner;

import static org.junit.Assert.*;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

/** Optional real-service/screenshot evidence; always restores the owned emulator's accessibility settings. */
final class NativeAccessibilityEvidence implements AutoCloseable {
    private final Instrumentation instrumentation;
    private final UiAutomation automation;
    private final String directory;
    private final boolean talkBack;
    private String services,enabled,touch;
    NativeAccessibilityEvidence(Instrumentation instrumentation) throws Exception {
        this.instrumentation=instrumentation;
        var arguments=InstrumentationRegistry.getArguments();
        directory=arguments.getString("evidenceDirectory");
        talkBack="true".equals(arguments.getString("talkbackAudit"));
        assertTrue("Actual service audit requires API24 or later",!talkBack||android.os.Build.VERSION.SDK_INT>=24);
        automation=android.os.Build.VERSION.SDK_INT>=24
                ?instrumentation.getUiAutomation(UiAutomation.FLAG_DONT_SUPPRESS_ACCESSIBILITY_SERVICES):instrumentation.getUiAutomation();
        assertNotNull("Native UiAutomation must connect",automation);
        var serviceInfo=automation.getServiceInfo();
        serviceInfo.flags|=android.accessibilityservice.AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        automation.setServiceInfo(serviceInfo);
        if(directory!=null){assertTrue("Owned evidence path",directory.matches("/sdcard/Download/[A-Za-z0-9_./-]+"));command("mkdir -p "+directory);}
        if(talkBack){
            services=command("settings get secure enabled_accessibility_services");
            enabled=command("settings get secure accessibility_enabled");touch=command("settings get secure touch_exploration_enabled");
            try {
                instrumentation.getTargetContext().getPackageManager().getPackageInfo("com.google.android.marvin.talkback",0);
                String component="com.google.android.marvin.talkback/com.google.android.marvin.talkback.TalkBackService";
                String desired=services.equals("null")||services.isEmpty()?component:services.contains(component)?services:services+":"+component;
                put("enabled_accessibility_services",desired);put("accessibility_enabled","1");put("touch_exploration_enabled","1");
                long until=SystemClock.elapsedRealtime()+15000;
                AccessibilityManager manager=(AccessibilityManager)instrumentation.getTargetContext().getSystemService(android.content.Context.ACCESSIBILITY_SERVICE);
                while(SystemClock.elapsedRealtime()<until&&(!manager.isTouchExplorationEnabled()||manager.getEnabledAccessibilityServiceList(-1).stream()
                        .noneMatch(service->service.getId().contains("talkback"))))SystemClock.sleep(50);
                assertTrue("Real TalkBack touch exploration must become active",manager.isTouchExplorationEnabled());
                assertTrue("Real TalkBack service must be enabled",manager.getEnabledAccessibilityServiceList(-1).stream().anyMatch(service->service.getId().contains("talkback")));
                System.out.println("TALKBACK_ACTIVE "+instrumentation.getTargetContext().getPackageManager().getPackageInfo("com.google.android.marvin.talkback",0).versionName);
            } catch(Throwable error){close();throw error;}
        }
        System.out.println("NATIVE_BUILD "+android.os.Build.FINGERPRINT+"; evidence="+directory+"; TalkBack="+talkBack);
    }
    void target(View view){
        assertNotNull("Native target",view);
        assertTrue("48dp width: "+view.getContentDescription(),view.getWidth()>=WackenTheme.dp(view.getContext(),48));
        assertTrue("48dp height: "+view.getContentDescription(),view.getHeight()>=WackenTheme.dp(view.getContext(),48));
        AccessibilityNodeInfo node=view.createAccessibilityNodeInfo();
        assertTrue("Action must have a meaningful label",node.getContentDescription()!=null&&!node.getContentDescription().toString().isBlank()
                ||node.getText()!=null&&!node.getText().toString().isBlank());
        assertTrue("Native action is exposed",node.isClickable());
        if(view instanceof TextView text&&text.getLayout()!=null)for(int line=0;line<text.getLineCount();line++)assertEquals("Essential action must not ellipsize",0,text.getLayout().getEllipsisCount(line));
        if(talkBack){
            assertTrue("TalkBack accessibility focus action",view.isAccessibilityFocused()||view.performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS,null));
            assertTrue("Actual accessibility focus",view.isAccessibilityFocused());
        }
        System.out.println("NATIVE_TARGET "+node.getClassName()+"; "+node.getText()+"; "+node.getContentDescription()+"; selected="+node.isSelected());
        node.recycle();
    }
    void capture(String name) throws Exception {
        if(directory==null)return;
        assertTrue(name.matches("[A-Za-z0-9_-]+"));instrumentation.waitForIdleSync();SystemClock.sleep(250);
        long until=SystemClock.elapsedRealtime()+5000;boolean nativeWindow=false;
        while(SystemClock.elapsedRealtime()<until){
            AccessibilityNodeInfo foreground=automation.getRootInActiveWindow();
            if(foreground!=null){
                String owner=String.valueOf(foreground.getPackageName());
                if(owner.equals("be.wacken.planner")){nativeWindow=true;foreground.recycle();break;}
                if(talkBack&&(owner.equals("com.google.android.permissioncontroller")||owner.equals("com.android.permissioncontroller"))
                        &&!foreground.findAccessibilityNodeInfosByText("Android Accessibility Suite").isEmpty()
                        &&!foreground.findAccessibilityNodeInfosByText("notifications").isEmpty()){
                    var deny=foreground.findAccessibilityNodeInfosByViewId("com.android.permissioncontroller:id/permission_deny_button");
                    if(deny.isEmpty())deny=foreground.findAccessibilityNodeInfosByText("Don’t allow");
                    if(!deny.isEmpty()){assertTrue(deny.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK));System.out.println("TALKBACK_NOTIFICATIONS_DECLINED_ON_TEST_EMULATOR");}
                    deny.forEach(AccessibilityNodeInfo::recycle);
                }
                foreground.recycle();
            }
            SystemClock.sleep(100);
        }
        if(!nativeWindow)command("screencap -p "+directory+"/"+name+"-blocked.png");
        assertTrue("Capture must show the actual native app window",nativeWindow);
        command("screencap -p "+directory+"/"+name+".png");
        assertTrue("Actual screenshot must be nonempty",Long.parseLong(command("stat -c %s "+directory+"/"+name+".png"))>0);
    }
    private void put(String key,String value) throws Exception {
        assertTrue(value.matches("[A-Za-z0-9_./:-]*"));command("settings put secure "+key+" "+value);
    }
    private void restore(String key,String value) throws Exception {
        if(value==null||value.equals("null"))command("settings delete secure "+key);else put(key,value);
    }
    private String command(String command) throws Exception {
        try(ParcelFileDescriptor result=automation.executeShellCommand(command);FileInputStream stream=new FileInputStream(result.getFileDescriptor())){
            var bytes=new java.io.ByteArrayOutputStream();byte[] buffer=new byte[1024];int count;
            while((count=stream.read(buffer))!=-1)bytes.write(buffer,0,count);
            return new String(bytes.toByteArray(),StandardCharsets.UTF_8).trim();
        }
    }
    @Override public void close() throws Exception {
        try {if(talkBack&&enabled!=null){restore("enabled_accessibility_services",services);restore("accessibility_enabled",enabled);restore("touch_exploration_enabled",touch);}}
        finally {instrumentation.getUiAutomation();}
    }
}
