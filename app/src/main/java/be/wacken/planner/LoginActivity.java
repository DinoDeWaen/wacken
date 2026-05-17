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
    private static final int COLOR_BACKGROUND = Color.rgb(29, 36, 38);
    private static final int COLOR_TEXT = Color.rgb(220, 224, 225);
    private static final int COLOR_MUTED = Color.rgb(162, 169, 171);
    private static final int COLOR_ACCENT = Color.rgb(255, 199, 44);

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
        title.setTextColor(Color.WHITE);
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

        email = input("Email", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        password = input("Password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        screen.addView(email);
        screen.addView(password);

        signIn = new Button(this);
        signIn.setAllCaps(false);
        signIn.setText("Sign in");
        signIn.setTextColor(Color.BLACK);
        signIn.setTypeface(Typeface.DEFAULT_BOLD);
        signIn.setBackgroundColor(COLOR_ACCENT);
        signIn.setOnClickListener(view -> signIn());
        screen.addView(signIn);

        message = new TextView(this);
        message.setTextColor(COLOR_TEXT);
        message.setGravity(Gravity.CENTER_HORIZONTAL);
        message.setPadding(0, dp(14), 0, 0);
        screen.addView(message);

        setContentView(screen);
    }

    private EditText input(String hint, int inputType) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setSingleLine(true);
        input.setInputType(inputType);
        input.setTextColor(Color.WHITE);
        input.setHintTextColor(COLOR_MUTED);
        input.setBackgroundColor(Color.rgb(41, 48, 50));
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
            message.setTextColor(Color.rgb(255, 115, 115));
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
                    message.setTextColor(Color.rgb(255, 115, 115));
                    message.setText(error.getMessage());
                });
            }
        }).start();
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
