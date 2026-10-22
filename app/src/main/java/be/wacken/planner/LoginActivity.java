package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ScrollView;

public final class LoginActivity extends Activity {
    private static final int COLOR_BACKGROUND = WackenTheme.BACKGROUND;
    private static final int COLOR_TEXT = WackenTheme.TEXT;
    private static final int COLOR_MUTED = WackenTheme.MUTED;
    private static final int COLOR_ACCENT = WackenTheme.GOLD;

    private EditText email;
    private EditText password;
    private TextView message;
    private Button signIn;
    private ScrollView scroll;
    private SignInState state;
    private static final class SignInState {
        String password="";
        volatile boolean busy,complete;
        volatile String error;
        volatile Runnable changed;
        void notifyUi(){Runnable callback=changed;if(callback!=null)callback.run();}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppearanceController.attach(this);
        state=getLastNonConfigurationInstance() instanceof SignInState retained?retained:new SignInState();

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        WackenTheme.backgroundColor(screen, COLOR_BACKGROUND);
        int padding = dp(24);
        screen.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Wacken Planner");
        WackenTheme.textColor(title, WackenTheme.AMBER);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        WackenTheme.heading(title);
        title.setGravity(Gravity.START);
        screen.addView(title);
        screen.addView(WackenTheme.appearanceToggle(this));

        TextView subtitle = new TextView(this);
        subtitle.setText("Sign in to sync group ratings");
        WackenTheme.textColor(subtitle, COLOR_MUTED);
        subtitle.setGravity(Gravity.START);subtitle.setTextSize(14);
        subtitle.setPadding(0, dp(6), 0, dp(18));
        screen.addView(subtitle);

        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(12), dp(12), dp(12), dp(12));
        panel.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        screen.addView(panel, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        email = input("Email", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        password = input("Password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        email.setId(R.id.login_email);
        password.setId(R.id.login_password);
        // Keep a rotation draft in memory only; do not serialize a password into saved state.
        password.setSaveEnabled(false);
        password.setText(state.password);
        panel.addView(email);
        panel.addView(password);

        signIn = WackenTheme.actionButton(this, "Sign in", WackenTheme.ButtonStyle.PREMIUM, view -> signIn());
        panel.addView(signIn);

        message = new TextView(this);
        message.setTextSize(16);message.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        WackenTheme.textColor(message, COLOR_TEXT);
        message.setGravity(Gravity.START);
        message.setPadding(dp(10), dp(10), dp(10), dp(10));
        message.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams messageLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        messageLayout.setMargins(0, dp(12), 0, 0);
        screen.addView(message, messageLayout);

        scroll=new ScrollView(this);WackenTheme.backgroundColor(scroll,COLOR_BACKGROUND);scroll.addView(screen);
        setContentView(scroll);
        state.changed=()->runOnUiThread(()->{if(!isFinishing()&&!isDestroyed()&&!isChangingConfigurations())renderSignIn();});
        renderSignIn();
        if(savedInstanceState!=null)scroll.post(()->scroll.scrollTo(0,savedInstanceState.getInt("scroll")));
    }

    private EditText input(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setInputType(inputType);
        WackenTheme.textColor(input,COLOR_TEXT);
        AppearanceController.bind(input,"hintColor",(view,palette)->((EditText)view).setHintTextColor(palette.muted()));
        input.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        WackenTheme.reviewInput(input);
        input.setContentDescription(hint);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(10));
        input.setLayoutParams(layout);
        return input;
    }

    private void signIn() {
        String enteredEmail = email.getText().toString().trim();
        String enteredPassword = password.getText().toString();
        if (enteredEmail.isBlank() || enteredPassword.isBlank()) {
            WackenTheme.textColor(message, WackenTheme.RED);
            message.setText("Enter email and password.");
            return;
        }

        if(state.busy)return;
        state.busy=true;state.error=null;renderSignIn();
        SignInState work=state;android.content.Context application=getApplicationContext();
        new Thread(()->{
            try {
                AuthSession session=new SupabaseAuthClient().signIn(enteredEmail,enteredPassword);
                new AuthSessionStore(application).save(session);work.complete=true;work.password="";
            } catch(Exception error){work.error=error.getMessage()==null?"Sign-in could not finish. Retry when connected.":error.getMessage();}
            finally {work.busy=false;work.notifyUi();}
        },"sign-in").start();
    }

    private void renderSignIn(){
        signIn.setEnabled(!state.busy);
        if(state.complete){startActivity(new Intent(this,MainActivity.class));finish();return;}
        WackenTheme.textColor(message,state.error==null?COLOR_MUTED:WackenTheme.RED);
        message.setText(state.busy?"Signing in...":state.error==null?"Cached festival data is kept when sign-in fails.":state.error);
    }
    @Override public Object onRetainNonConfigurationInstance(){state.password=password==null?"":password.getText().toString();return state;}
    @Override protected void onSaveInstanceState(Bundle saved){saved.putInt("scroll",scroll.getScrollY());super.onSaveInstanceState(saved);}
    @Override protected void onDestroy(){state.changed=null;super.onDestroy();}

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
