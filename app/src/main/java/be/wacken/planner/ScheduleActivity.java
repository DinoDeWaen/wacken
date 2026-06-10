package be.wacken.planner;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;

import be.wacken.planner.application.GenerateSharedScheduleUseCase;
import be.wacken.planner.application.ScheduleDay;
import be.wacken.planner.application.SharedSchedule;
import be.wacken.planner.application.SharedScheduleStatus;
import be.wacken.planner.application.TimelineSlot;

public final class ScheduleActivity extends Activity {
    private static final int COLOR_BACKGROUND = Color.rgb(29, 36, 38);
    private static final int COLOR_PANEL = Color.rgb(38, 46, 48);
    private static final int COLOR_GRID = Color.rgb(67, 75, 78);
    private static final int COLOR_TEXT = Color.rgb(220, 224, 225);
    private static final int COLOR_MUTED = Color.rgb(162, 169, 171);
    private static final int COLOR_ACCENT = Color.rgb(255, 56, 92);
    private static final int COLOR_AMBER = Color.rgb(255, 199, 44);
    private static final int HOUR_HEIGHT_DP = 72;
    private static final int TIME_LABEL_WIDTH_DP = 54;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(render());
    }

    private ScrollView render() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setBackgroundColor(COLOR_BACKGROUND);
        int padding = dp(16);
        screen.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Group Schedule");
        title.setTextColor(COLOR_AMBER);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        screen.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("MVP2 timeline from shared ratings and conflict rules");
        subtitle.setTextColor(COLOR_MUTED);
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        subtitle.setPadding(0, dp(4), 0, dp(16));
        screen.addView(subtitle);

        Button back = new Button(this);
        back.setAllCaps(false);
        back.setText("Back to bands");
        back.setTextColor(Color.WHITE);
        back.setTypeface(Typeface.DEFAULT_BOLD);
        back.setBackgroundColor(Color.rgb(49, 56, 58));
        back.setOnClickListener(view -> finish());
        screen.addView(back, fullWidthButtonLayout());

        try {
            AppRepositories repositories = new AppRepositories(this);
            SharedSchedule schedule = new GenerateSharedScheduleUseCase(
                    repositories.performances(),
                    repositories.ratings()
            ).generate();
            addSchedule(screen, schedule);
        } catch (Exception error) {
            screen.addView(message("Schedule could not be generated: " + error.getMessage(), COLOR_ACCENT));
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(COLOR_BACKGROUND);
        scrollView.addView(screen);
        return scrollView;
    }

    private void addSchedule(LinearLayout screen, SharedSchedule schedule) {
        if (schedule.status() != SharedScheduleStatus.GENERATED) {
            screen.addView(message(schedule.message(), COLOR_MUTED));
            return;
        }
        if (schedule.days().isEmpty()) {
            screen.addView(message("No selected performances are available yet.", COLOR_MUTED));
            return;
        }
        for (ScheduleDay day : schedule.days()) {
            TextView dayTitle = sectionTitle(day.date().format(DATE));
            screen.addView(dayTitle);
            screen.addView(dayCalendar(day));
        }
    }

    private TextView sectionTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        title.setTextColor(COLOR_AMBER);
        title.setTextSize(18);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, dp(16), 0, dp(6));
        return title;
    }

    private FrameLayout dayCalendar(ScheduleDay day) {
        if (day.slots().isEmpty()) {
            FrameLayout empty = new FrameLayout(this);
            empty.addView(message("No selected performances.", COLOR_MUTED));
            return empty;
        }
        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forSlots(day.slots());
        int calendarHeight = dp(layout.hourCount() * HOUR_HEIGHT_DP);
        FrameLayout calendar = new FrameLayout(this);
        calendar.setBackgroundColor(COLOR_BACKGROUND);
        calendar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                calendarHeight
        ));

        for (int hour = 0; hour <= layout.hourCount(); hour++) {
            calendar.addView(hourLine(layout, hour), hourLineLayout(hour));
        }
        for (TimelineSlot slot : day.slots()) {
            calendar.addView(slotView(slot), slotLayout(layout, slot));
        }
        return calendar;
    }

    private TextView hourLine(ScheduleCalendarLayout layout, int hourOffset) {
        TextView line = new TextView(this);
        line.setText(layout.hourLabel(hourOffset) + " ─────────────────");
        line.setTextColor(COLOR_GRID);
        line.setTextSize(11);
        line.setSingleLine(true);
        return line;
    }

    private FrameLayout.LayoutParams hourLineLayout(int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(18)
        );
        params.topMargin = dp(hourOffset * HOUR_HEIGHT_DP);
        return params;
    }

    private FrameLayout.LayoutParams slotLayout(ScheduleCalendarLayout layout, TimelineSlot slot) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dp(Math.max(58, layout.durationMinutes(slot) * HOUR_HEIGHT_DP / 60))
        );
        params.leftMargin = dp(TIME_LABEL_WIDTH_DP);
        params.topMargin = dp(layout.topOffsetMinutes(slot) * HOUR_HEIGHT_DP / 60);
        params.rightMargin = 0;
        return params;
    }

    private LinearLayout slotView(TimelineSlot slot) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackground(slotBackground());
        panel.setPadding(dp(10), dp(6), dp(10), dp(6));

        TextView band = new TextView(this);
        band.setText(slot.bandName() + " " + stars(slot.rating()));
        band.setTextColor(COLOR_TEXT);
        band.setTextSize(18);
        band.setTypeface(Typeface.DEFAULT_BOLD);
        panel.addView(band);

        TextView facts = new TextView(this);
        facts.setText(slot.stageName() + " | " + slot.start().format(TIME) + " - " + slot.end().format(TIME));
        facts.setTextColor(COLOR_MUTED);
        facts.setPadding(0, dp(3), 0, 0);
        panel.addView(facts);

        TextView decision = new TextView(this);
        decision.setText(slot.optional() ? "OPTIONAL" : slot.decisionStatus().name());
        decision.setTextColor(slot.optional() ? COLOR_AMBER : COLOR_ACCENT);
        decision.setTypeface(Typeface.DEFAULT_BOLD);
        decision.setPadding(0, dp(5), 0, 0);
        panel.addView(decision);

        slot.lostAlternativeBandName().ifPresent(lost -> {
            TextView alternative = new TextView(this);
            String alternativeText = "Lost alternative: " + lost
                    + slot.lostAlternativeRating()
                    .map(rating -> " " + stars(rating))
                    .orElse("");
            alternative.setText(alternativeText);
            alternative.setTextColor(COLOR_MUTED);
            alternative.setPadding(0, dp(5), 0, 0);
            panel.addView(alternative);
        });
        return panel;
    }

    private GradientDrawable slotBackground() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(COLOR_PANEL);
        drawable.setStroke(dp(1), COLOR_ACCENT);
        drawable.setCornerRadius(dp(4));
        return drawable;
    }

    private String stars(int rating) {
        int safeRating = Math.max(0, Math.min(5, rating));
        StringBuilder text = new StringBuilder(5);
        for (int index = 0; index < 5; index++) {
            text.append(index < safeRating ? "★" : "☆");
        }
        return text.toString();
    }

    private TextView message(String text, int color) {
        TextView message = new TextView(this);
        message.setText(text);
        message.setTextColor(color);
        message.setGravity(Gravity.CENTER_HORIZONTAL);
        message.setPadding(0, dp(28), 0, dp(28));
        return message;
    }

    private LinearLayout.LayoutParams fullWidthButtonLayout() {
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(12));
        return layout;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }
}
