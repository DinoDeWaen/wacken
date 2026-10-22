package be.wacken.planner;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        AppearanceController.bind(button,"buttonStyle",(view,palette)->{
            Button target=(Button)view;
            boolean primary=style==ButtonStyle.PRIMARY||style==ButtonStyle.PREMIUM;
            target.setTextColor(primary?palette.onAccent():style==ButtonStyle.DANGER?palette.error():palette.text());
            target.setBackground(focusBackground(target.getContext(),primary?palette.accent():palette.panel(),
                    style==ButtonStyle.DANGER?palette.error():palette.line(),
                    primary?palette.accent():palette.raised(),primary?palette.onAccent():palette.text()));
        });
        button.setOnClickListener(listener);
        return button;
    }

    static void ratingChoice(TextView choice) {
        AppearanceController.bind(choice,"buttonStyle",(view,palette)->{
            TextView target=(TextView)view;
            target.setTextColor(palette.accent());
            target.setBackground(focusBackground(target.getContext(),target.isSelected()?palette.raised():palette.panel(),
                    target.isSelected()?palette.accent():palette.line(),palette.raised(),palette.text()));
        });
    }

    private static StateListDrawable focusBackground(Context context,int fill,int border,int focusFill,int focusBorder) {
        StateListDrawable states=new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused},buttonPanel(context,focusFill,focusBorder,3));
        states.addState(new int[]{},buttonPanel(context,fill,border,1));
        return states;
    }

    private static GradientDrawable buttonPanel(Context context,int fill,int border,int width) {
        GradientDrawable panel=new GradientDrawable();
        panel.setColor(fill);panel.setStroke(dp(context,width),border);panel.setCornerRadius(dp(context,6));
        return panel;
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
        textColor(button,TEXT);
        button.setContentDescription(description);
        button.setPadding(0, 0, 0, 0);
        button.setBackground(panelBackground(context, PANEL, accentColor == WHITE ? GRID : accentColor, 5));
        button.setOnClickListener(listener);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(context, Math.max(48,sizeDp)), dp(context, Math.max(48,sizeDp)));
        layout.setMargins(dp(context, 4), 0, dp(context, 4), 0);
        button.setLayoutParams(layout);
        return button;
    }

    static GradientDrawable panelBackground(Context context, int fillColor, int borderColor, int cornerDp) {
        GradientDrawable drawable = new GradientDrawable();
        AppearanceController.bindPanel(context,drawable,fillColor,borderColor,cornerDp);
        return drawable;
    }

    // These are explicit legacy semantic token arguments, never inferred view colors.
    static int resolve(int token,AppearancePreference.Palette palette) {
        return switch(token) {
            case VOID,BACKGROUND -> palette.background();
            case PANEL -> palette.panel();
            case ELEVATED_PANEL,BLOOD_RED -> palette.raised();
            case TEXT,WHITE -> palette.text();
            case MUTED,FAINT,STEEL_GREY -> palette.muted();
            case RED,GOLD,AMBER,SUCCESS_GREEN -> palette.accent();
            case GRID -> palette.line();
            case BLACK -> palette.onAccent();
            default -> token;
        };
    }

    static void textColor(TextView view,int token) {
        AppearanceController.bind(view,"textColor",(target,palette)->((TextView)target).setTextColor(resolve(token,palette)));
    }

    static void backgroundColor(View view,int token) {
        AppearanceController.bind(view,"backgroundColor",(target,palette)->target.setBackgroundColor(resolve(token,palette)));
    }

    static void heading(TextView view) {
        textColor(view,AMBER);
        AppearanceController.bind(view,"headingTypeface",(target,palette)->((TextView)target).setTypeface(
                palette.dark()?Typeface.DEFAULT_BOLD:Typeface.create("serif",Typeface.BOLD)));
    }

    static Button appearanceToggle(Context context) {
        Button toggle=actionButton(context,AppearanceController.toggleLabel(context),ButtonStyle.SECONDARY,
                view->AppearanceController.toggle(view.getContext()));
        AppearanceController.bind(toggle,"toggleLabel",(view,palette)->{
            String label=palette.dark()?"Switch to light":"Switch to dark";
            ((Button)view).setText(label);view.setContentDescription(label);
        });
        return toggle;
    }

    static View appearanceChoices(Context context) {
        android.widget.RadioGroup group=new android.widget.RadioGroup(context);
        group.setOrientation(LinearLayout.VERTICAL);
        group.setSaveEnabled(false); // The persisted preference, not stale hierarchy state, is authoritative.
        String[] labels={"System","Ironline dark","Field Notes light"};
        for(String label:labels) {
            android.widget.RadioButton choice=new android.widget.RadioButton(context);
            choice.setId(View.generateViewId());choice.setText(label);choice.setTextSize(16);
            choice.setMinHeight(dp(context,48));choice.setSaveEnabled(false);
            textColor(choice,TEXT);
            AppearanceController.bind(choice,"radioTint",(view,palette)->
                    ((android.widget.RadioButton)view).setButtonTintList(android.content.res.ColorStateList.valueOf(palette.accent())));
            group.addView(choice);
        }
        group.setOnCheckedChangeListener((parent,id)->{
            if(Boolean.TRUE.equals(parent.getTag()))return;
            for(int i=0;i<parent.getChildCount();i++)if(parent.getChildAt(i).getId()==id)
                AppearanceController.choose(parent.getContext(),AppearancePreference.values()[i]);
        });
        AppearanceController.bind(group,"selectedPreference",(view,palette)->{
            android.widget.RadioGroup target=(android.widget.RadioGroup)view;
            target.setTag(Boolean.TRUE);
            target.check(target.getChildAt(AppearanceController.preference(target.getContext()).ordinal()).getId());
            target.setTag(Boolean.FALSE);
        });
        return group;
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
        button.setMinHeight(dp(context,48));
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
