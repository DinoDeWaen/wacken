package be.wacken.planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

import be.wacken.planner.application.GenerateSharedScheduleUseCase;
import be.wacken.planner.application.ScheduleDay;
import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.SharedSchedule;
import be.wacken.planner.application.SharedScheduleStatus;
import be.wacken.planner.application.TimelineSlot;
import be.wacken.planner.domain.StageWalkingTimePolicy;

public final class ScheduleActivity extends Activity {
    private static final int COLOR_BACKGROUND = Color.rgb(29, 36, 38);
    private static final int COLOR_PANEL = Color.rgb(38, 46, 48);
    private static final int COLOR_GRID = Color.rgb(67, 75, 78);
    private static final int COLOR_TEXT = Color.rgb(220, 224, 225);
    private static final int COLOR_MUTED = Color.rgb(162, 169, 171);
    private static final int COLOR_ACCENT = Color.rgb(255, 56, 92);
    private static final int COLOR_AMBER = Color.rgb(255, 199, 44);
    private static final int HOUR_HEIGHT_DP = 72;
    private static final int TIME_LABEL_WIDTH_DP = 78;
    private static final int STAGE_COLUMN_WIDTH_DP = 190;
    private static final int STAGE_HEADER_HEIGHT_DP = 32;
    private static final int COLUMN_GAP_DP = 6;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEEE yyyy-MM-dd", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private final ScheduleManualSelections manualSelections = new ScheduleManualSelections();

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
                    repositories.ratings(),
                    repositories.distances()
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

    private View dayCalendar(ScheduleDay day) {
        if (day.slots().isEmpty()) {
            FrameLayout empty = new FrameLayout(this);
            empty.addView(message("No selected performances.", COLOR_MUTED));
            return empty;
        }
        java.util.List<ScheduleDecisionCandidate> visibleCandidates = new java.util.ArrayList<>();
        for (TimelineSlot slot : day.slots()) {
            visibleCandidates.add(manualSelections.visibleCandidate(slot));
        }
        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forCandidates(visibleCandidates, day.date());
        int calendarHeight = dp(STAGE_HEADER_HEIGHT_DP + (layout.hourCount() * HOUR_HEIGHT_DP));
        int calendarWidth = dp(TIME_LABEL_WIDTH_DP + (layout.stageColumnCount() * STAGE_COLUMN_WIDTH_DP));
        FrameLayout calendar = new FrameLayout(this);
        calendar.setBackgroundColor(COLOR_BACKGROUND);
        calendar.setLayoutParams(new FrameLayout.LayoutParams(
                calendarWidth,
                calendarHeight
        ));

        for (int column = 0; column < layout.stageColumns().size(); column++) {
            calendar.addView(stageHeader(layout.stageColumns().get(column)), stageHeaderLayout(column));
        }
        for (int hour = 0; hour <= layout.hourCount(); hour++) {
            calendar.addView(hourLine(layout, hour), hourLineLayout(hour));
        }
        for (int index = 0; index < day.slots().size(); index++) {
            TimelineSlot slot = day.slots().get(index);
            ScheduleDecisionCandidate visible = manualSelections.visibleCandidate(slot);
            calendar.addView(slotStartTimeLabel(visible), slotStartTimeLayout(layout, visible));
            calendar.addView(slotEndTimeLabel(visible), slotEndTimeLayout(layout, visible));
            calendar.addView(slotView(slot, visible, layout.durationMinutes(visible)), slotLayout(layout, visible));
            if (index < day.slots().size() - 1) {
                ScheduleDecisionCandidate next = manualSelections.visibleCandidate(day.slots().get(index + 1));
                calendar.addView(walkingMarker(slot), walkingMarkerLayout(layout, visible, next));
            }
        }
        HorizontalScrollView horizontal = new HorizontalScrollView(this);
        horizontal.setHorizontalScrollBarEnabled(true);
        horizontal.setFillViewport(false);
        horizontal.setBackgroundColor(COLOR_BACKGROUND);
        horizontal.addView(calendar);
        return horizontal;
    }

    private TextView stageHeader(String stageName) {
        TextView header = new TextView(this);
        header.setText(stageName);
        header.setTextColor(COLOR_AMBER);
        header.setTextSize(12);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setGravity(Gravity.CENTER);
        header.setSingleLine(true);
        header.setEllipsize(TextUtils.TruncateAt.END);
        header.setBackgroundColor(Color.rgb(24, 30, 32));
        return header;
    }

    private FrameLayout.LayoutParams stageHeaderLayout(int column) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(STAGE_COLUMN_WIDTH_DP - COLUMN_GAP_DP),
                dp(STAGE_HEADER_HEIGHT_DP - 4)
        );
        params.leftMargin = dp(TIME_LABEL_WIDTH_DP + (column * STAGE_COLUMN_WIDTH_DP));
        return params;
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
        params.topMargin = dp(STAGE_HEADER_HEIGHT_DP + (hourOffset * HOUR_HEIGHT_DP));
        return params;
    }

    private FrameLayout.LayoutParams slotLayout(ScheduleCalendarLayout layout, ScheduleDecisionCandidate candidate) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(STAGE_COLUMN_WIDTH_DP - COLUMN_GAP_DP),
                dp(Math.max(58, layout.durationMinutes(candidate) * HOUR_HEIGHT_DP / 60))
        );
        params.leftMargin = dp(TIME_LABEL_WIDTH_DP + (layout.stageColumnIndex(candidate) * STAGE_COLUMN_WIDTH_DP));
        params.topMargin = dp(STAGE_HEADER_HEIGHT_DP + (layout.topOffsetMinutes(candidate) * HOUR_HEIGHT_DP / 60));
        return params;
    }

    private TextView slotStartTimeLabel(ScheduleDecisionCandidate candidate) {
        return eventTimeLabel(candidate.start().format(TIME) + " ─");
    }

    private TextView slotEndTimeLabel(ScheduleDecisionCandidate candidate) {
        return eventTimeLabel(candidate.end().format(TIME) + " ─");
    }

    private TextView eventTimeLabel(String text) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextColor(Color.WHITE);
        label.setTextSize(11);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setSingleLine(true);
        return label;
    }

    private FrameLayout.LayoutParams slotStartTimeLayout(ScheduleCalendarLayout layout, ScheduleDecisionCandidate candidate) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(TIME_LABEL_WIDTH_DP),
                dp(18)
        );
        params.topMargin = dp(STAGE_HEADER_HEIGHT_DP + (layout.topOffsetMinutes(candidate) * HOUR_HEIGHT_DP / 60));
        return params;
    }

    private FrameLayout.LayoutParams slotEndTimeLayout(ScheduleCalendarLayout layout, ScheduleDecisionCandidate candidate) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(TIME_LABEL_WIDTH_DP),
                dp(18)
        );
        int top = (layout.endOffsetMinutes(candidate) * HOUR_HEIGHT_DP / 60) - 18;
        params.topMargin = dp(STAGE_HEADER_HEIGHT_DP + Math.max(0, top));
        return params;
    }

    private TextView walkingMarker(TimelineSlot slot) {
        TextView marker = new TextView(this);
        String text = slot.walkingMinutesToNext().isPresent()
                ? "Walk " + slot.walkingMinutesToNext().getAsInt() + "m"
                : "Walk ?";
        marker.setText(text);
        marker.setTextColor(COLOR_AMBER);
        marker.setTextSize(12);
        marker.setTypeface(Typeface.DEFAULT_BOLD);
        marker.setGravity(Gravity.CENTER_VERTICAL);
        marker.setSingleLine(true);
        marker.setEllipsize(TextUtils.TruncateAt.END);
        return marker;
    }

    private FrameLayout.LayoutParams walkingMarkerLayout(
            ScheduleCalendarLayout layout,
            ScheduleDecisionCandidate from,
            ScheduleDecisionCandidate to
    ) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(TIME_LABEL_WIDTH_DP),
                dp(22)
        );
        params.leftMargin = 0;
        params.topMargin = dp(STAGE_HEADER_HEIGHT_DP
                + Math.max(0, (layout.walkingMarkerOffsetMinutes(from, to) * HOUR_HEIGHT_DP / 60) - 11));
        return params;
    }

    private LinearLayout slotView(TimelineSlot slot, ScheduleDecisionCandidate visible, int blockMinutes) {
        ScheduleBlockContent content = ScheduleBlockContent.from(slot, visible, blockMinutes);
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackground(slotBackground());
        panel.setPadding(dp(10), dp(6), dp(10), dp(6));
        panel.setClickable(true);
        panel.setOnClickListener(view -> showDecisionDetails(slot));

        TextView band = new TextView(this);
        band.setText(content.bandLine());
        band.setTextColor(COLOR_TEXT);
        band.setTextSize(16);
        band.setTypeface(Typeface.DEFAULT_BOLD);
        band.setSingleLine(true);
        band.setEllipsize(TextUtils.TruncateAt.END);
        panel.addView(band);

        TextView facts = new TextView(this);
        facts.setText(content.stageLine());
        facts.setTextColor(COLOR_MUTED);
        facts.setSingleLine(true);
        facts.setEllipsize(TextUtils.TruncateAt.END);
        facts.setPadding(0, dp(3), 0, 0);
        panel.addView(facts);

        content.lostAlternativeLine().ifPresent(lost -> {
            TextView alternative = new TextView(this);
            alternative.setText(lost);
            alternative.setTextColor(COLOR_MUTED);
            alternative.setSingleLine(true);
            alternative.setEllipsize(TextUtils.TruncateAt.END);
            alternative.setPadding(0, dp(5), 0, 0);
            panel.addView(alternative);
        });
        return panel;
    }

    private void showDecisionDetails(TimelineSlot slot) {
        ScrollView scroll = new ScrollView(this);
        scroll.setBackground(detailDialogBackground());

        LinearLayout detail = new LinearLayout(this);
        detail.setOrientation(LinearLayout.VERTICAL);
        detail.setPadding(dp(18), dp(16), dp(18), dp(10));
        detail.setBackground(detailDialogBackground());

        TextView title = detailText(slot.bandName(), COLOR_TEXT, 24, true);
        title.setPadding(0, 0, 0, dp(14));
        detail.addView(title);

        detail.addView(detailText("Chosen act", COLOR_ACCENT, 12, true));
        final AlertDialog[] dialog = new AlertDialog[1];
        java.util.List<ScheduleDecisionCandidate> candidates = manualSelections.detailCandidates(slot);
        detail.addView(candidateView(slot, candidates.get(0), candidates, () -> dialog[0].dismiss()));

        detail.addView(detailText("Alternatives", COLOR_ACCENT, 12, true));
        boolean hasAlternatives = false;
        for (int index = 1; index < candidates.size(); index++) {
            ScheduleDecisionCandidate candidate = candidates.get(index);
            if (!candidate.selected()) {
                detail.addView(candidateView(slot, candidate, candidates, () -> dialog[0].dismiss()));
                hasAlternatives = true;
            }
        }
        if (!hasAlternatives) {
            detail.addView(detailText("No alternatives available.", COLOR_MUTED, 14, false));
        }
        Button close = new Button(this);
        close.setAllCaps(false);
        close.setText("Close");
        close.setTextColor(COLOR_AMBER);
        close.setTypeface(Typeface.DEFAULT_BOLD);
        close.setBackground(slotBackground());
        close.setOnClickListener(view -> dialog[0].dismiss());
        LinearLayout.LayoutParams closeLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        closeLayout.setMargins(0, dp(18), 0, 0);
        detail.addView(close, closeLayout);
        scroll.addView(detail);

        dialog[0] = new AlertDialog.Builder(this)
                .setView(scroll)
                .show();
        if (dialog[0].getWindow() != null) {
            dialog[0].getWindow().setBackgroundDrawable(detailDialogBackground());
        }
    }

    private LinearLayout candidateView(
            TimelineSlot slot,
            ScheduleDecisionCandidate candidate,
            java.util.List<ScheduleDecisionCandidate> candidates,
            Runnable afterSelect
    ) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(0, dp(6), 0, dp(10));

        TextView name = detailText(candidate.bandName() + " " + stars(candidate.rating()),
                candidate.selected() ? COLOR_TEXT : COLOR_MUTED,
                16,
                candidate.selected());
        name.setClickable(true);
        name.setOnClickListener(view -> {
            openBandDetail(candidate);
            afterSelect.run();
        });
        panel.addView(name);

        TextView facts = detailText(
                candidate.stageName() + " | " + candidate.start().format(TIME) + " - " + candidate.end().format(TIME),
                COLOR_MUTED,
                13,
                false
        );
        panel.addView(facts);

        TextView status = detailText(candidate.status(), candidate.selected() ? COLOR_ACCENT : COLOR_AMBER, 12, true);
        panel.addView(status);
        walkingContext(candidate, candidates).ifPresent(context ->
                panel.addView(detailText(context, COLOR_AMBER, 12, true))
        );
        if (!candidate.selected()) {
            Button select = new Button(this);
            select.setAllCaps(false);
            select.setText("Select as act");
            select.setTextColor(Color.WHITE);
            select.setTypeface(Typeface.DEFAULT_BOLD);
            select.setBackground(slotBackground());
            select.setOnClickListener(view -> {
                manualSelections.select(slot, candidate);
                setContentView(render());
                afterSelect.run();
            });
            panel.addView(select);
        }
        return panel;
    }

    private java.util.Optional<String> walkingContext(
            ScheduleDecisionCandidate candidate,
            java.util.List<ScheduleDecisionCandidate> candidates
    ) {
        java.util.List<String> details = new java.util.ArrayList<>();
        for (ScheduleDecisionCandidate other : candidates) {
            if (other == candidate || other.bandName().equals(candidate.bandName())) {
                continue;
            }
            int minutes = StageWalkingTimePolicy.defaultWalkingMinutes(candidate.stageName(), other.stageName());
            details.add(minutes + " min to " + other.bandName());
        }
        if (details.isEmpty()) {
            return java.util.Optional.empty();
        }
        return java.util.Optional.of("Walk: " + String.join(" | ", details));
    }

    private void openBandDetail(ScheduleDecisionCandidate candidate) {
        Intent intent = new Intent(this, BandDetailActivity.class);
        intent.putExtra(BandDetailActivity.EXTRA_BAND_NAME, candidate.bandName());
        intent.putExtra(BandDetailActivity.EXTRA_STAGE, candidate.stageName());
        intent.putExtra(BandDetailActivity.EXTRA_DATE, candidate.start().toLocalDate().toString());
        intent.putExtra(BandDetailActivity.EXTRA_TIME,
                candidate.start().format(TIME) + " - " + candidate.end().format(TIME));
        startActivity(intent);
    }

    private TextView detailText(String text, int color, int size, boolean bold) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(color);
        view.setTextSize(size);
        view.setPadding(0, dp(2), 0, dp(2));
        if (bold) {
            view.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return view;
    }

    private GradientDrawable detailDialogBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.rgb(10, 13, 14),
                        COLOR_BACKGROUND,
                        Color.rgb(48, 38, 28),
                        Color.rgb(13, 16, 17)
                }
        );
        drawable.setStroke(dp(1), COLOR_ACCENT);
        drawable.setCornerRadius(dp(4));
        return drawable;
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
