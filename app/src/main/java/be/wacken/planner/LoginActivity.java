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

        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.setBackgroundColor(COLOR_BACKGROUND);
        int padding = dp(24);
        screen.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Wacken Planner");
        title.setTextColor(WackenTheme.AMBER);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Sign in to sync group ratings");
        subtitle.setTextColor(COLOR_MUTED);
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
        panel.addView(email);
        panel.addView(password);

        signIn = WackenTheme.actionButton(this, "Sign in", WackenTheme.ButtonStyle.PREMIUM, view -> signIn());
        panel.addView(signIn);

        message = new TextView(this);
        message.setTextColor(COLOR_TEXT);
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
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(COLOR_MUTED);
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
            message.setTextColor(WackenTheme.RED);
            message.setText("Enter email and password.");
            return;
        }

        signIn.setEnabled(false);
        message.setTextColor(COLOR_MUTED);
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
                    message.setTextColor(WackenTheme.RED);
                    message.setText(error.getMessage());
                });
            }
        }).start();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
