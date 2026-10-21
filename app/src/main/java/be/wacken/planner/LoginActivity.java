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

public final class LoginActivity extends Activity {
    private static final int COLOR_BACKGROUND = WackenTheme.BACKGROUND;
    private static final int COLOR_TEXT = WackenTheme.TEXT;
    private static final int COLOR_MUTED = WackenTheme.MUTED;
    private static final int COLOR_ACCENT = WackenTheme.GOLD;

    private EditText email;
    private EditText password;
    private TextView message;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppearanceController.attach(this);

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
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.addView(title);
        screen.addView(WackenTheme.appearanceToggle(this));

        TextView subtitle = new TextView(this);
        subtitle.setText("Sign in to sync group ratings");
        WackenTheme.textColor(subtitle, COLOR_MUTED);
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
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
        if(getLastNonConfigurationInstance() instanceof String draft)password.setText(draft);
        panel.addView(email);
        panel.addView(password);

        signIn = WackenTheme.actionButton(this, "Sign in", WackenTheme.ButtonStyle.PREMIUM, view -> signIn());
        panel.addView(signIn);

        message = new TextView(this);
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

        setContentView(screen);
    }

    private EditText input(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setInputType(inputType);
        WackenTheme.textColor(input,COLOR_TEXT);
        AppearanceController.bind(input,"hintColor",(view,palette)->((EditText)view).setHintTextColor(palette.muted()));
        input.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, WackenTheme.GRID, 6));
        input.setPadding(dp(10), 0, dp(10), 0);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(48)
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

        signIn.setEnabled(false);
        WackenTheme.textColor(message, COLOR_MUTED);
        message.setText("Signing in...");
        new Thread(() -> {
            try {
                AuthSession session = new SupabaseAuthClient().signIn(enteredEmail, enteredPassword);
                new AuthSessionStore(this).save(session);
                runOnUiThread(() -> {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    signIn.setEnabled(true);
                    WackenTheme.textColor(message, WackenTheme.RED);
                    message.setText(error.getMessage());
                });
            }
        }).start();
    }

    @Override public Object onRetainNonConfigurationInstance() {
        return password==null?null:password.getText().toString();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
