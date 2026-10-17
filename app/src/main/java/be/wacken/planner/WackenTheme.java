package be.wacken.planner;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

final class WackenTheme {
    static final int VOID = 0xFF0B0F10;
    static final int BACKGROUND = 0xFF121819;
    static final int PANEL = 0xFF20282A;
    static final int ELEVATED_PANEL = 0xFF263033;
    static final int GRID = 0xFF434B4E;
    static final int TEXT = 0xFFDCE0E1;
    static final int MUTED = 0xFFA2A9AB;
    static final int FAINT = 0xFF697174;
    static final int GOLD = 0xFFFFD24A;
    static final int AMBER = 0xFFFFC72C;
    static final int RED = 0xFFFF3B6B;
    static final int BLOOD_RED = 0xFF7A1F2F;
    static final int STEEL_GREY = 0xFFAAB3B7;
    static final int SUCCESS_GREEN = 0xFF1ED760;
    static final int WHITE = 0xFFFFFFFF;
    static final int BLACK = 0xFF000000;

    private WackenTheme() {
    }

    static Button actionButton(Context context, String text, ButtonStyle style, View.OnClickListener listener) {
        Button button = baseButton(context, text);
        button.setTextColor(style.textColor());
        button.setBackground(panelBackground(context, style.fillColor(), style.borderColor(), 6));
        button.setOnClickListener(listener);
        return button;
    }

    static Button iconButton(
            Context context,
            String icon,
            String description,
            int accentColor,
            int sizeDp,
            View.OnClickListener listener
    ) {
        Button button = baseButton(context, icon);
        button.setTextSize(22);
        button.setTextColor(WHITE);
        button.setContentDescription(description);
        button.setPadding(0, 0, 0, 0);
        button.setBackground(panelBackground(context, PANEL, accentColor == WHITE ? GRID : accentColor, 5));
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(context, sizeDp), dp(context, sizeDp));
        layout.setMargins(dp(context, 4), 0, dp(context, 4), 0);
        button.setLayoutParams(layout);
        return button;
    }

    static GradientDrawable panelBackground(Context context, int fillColor, int borderColor, int cornerDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fillColor);
        drawable.setStroke(dp(context, 1), borderColor);
        drawable.setCornerRadius(dp(context, cornerDp));
        return drawable;
    }

    static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density);
    }

    private static Button baseButton(Context context, String text) {
        Button button = new Button(context);
        button.setAllCaps(false);
        button.setText(text);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setMinWidth(0);
        button.setMinHeight(0);
        return button;
    }

    enum ButtonStyle {
        PRIMARY(RED, WHITE),
        PREMIUM(GOLD, BLACK),
        SECONDARY(PANEL, WHITE),
        DANGER(BLOOD_RED, WHITE);

        private final int fillColor;
        private final int textColor;

        ButtonStyle(int fillColor, int textColor) {
            this.fillColor = fillColor;
            this.textColor = textColor;
        }

        int fillColor() {
            return fillColor;
        }

        int textColor() {
            return textColor;
        }

        int borderColor() {
            if (this == PRIMARY) {
                return RED;
            }
            if (this == PREMIUM) {
                return GOLD;
            }
            if (this == DANGER) {
                return BLOOD_RED;
            }
            return GRID;
        }
    }
}
