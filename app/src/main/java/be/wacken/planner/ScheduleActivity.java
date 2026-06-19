package be.wacken.planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;

import be.wacken.planner.application.GenerateSharedScheduleUseCase;
import be.wacken.planner.application.ScheduleDay;
import be.wacken.planner.application.ScheduleDecisionCandidate;
import be.wacken.planner.application.SharedSchedule;
import be.wacken.planner.application.SharedScheduleStatus;
import be.wacken.planner.application.TimelineSlot;
import be.wacken.planner.domain.StageWalkingTimePolicy;

public final class ScheduleActivity extends Activity {
    private static final int COLOR_BACKGROUND = WackenTheme.BACKGROUND;
    private static final int COLOR_PANEL = WackenTheme.ELEVATED_PANEL;
    private static final int COLOR_GRID = WackenTheme.GRID;
    private static final int COLOR_TEXT = WackenTheme.TEXT;
    private static final int COLOR_MUTED = WackenTheme.MUTED;
    private static final int COLOR_ACCENT = WackenTheme.RED;
    private static final int COLOR_AMBER = WackenTheme.AMBER;
    private static final int HOUR_WIDTH_DP = 156;
    private static final int STAGE_LABEL_WIDTH_DP = 142;
    private static final int STAGE_ROW_HEIGHT_DP = 92;
    private static final int TIME_HEADER_HEIGHT_DP = 38;
    private static final int ROW_GAP_DP = 8;
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("EEEE yyyy-MM-dd", Locale.ENGLISH);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");
    private ScheduleManualSelections manualSelections = new ScheduleManualSelections();
    private AppRepositories repositories;
    private boolean hideBarred;
    private int selectedHideThreshold;
    private LocalDate selectedScheduleDate;
    private boolean scheduleLocksRequested;
    private boolean scheduleLocksLoaded;
    private String scheduleLockWarning;

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

        Button back = WackenTheme.actionButton(this, "Back to bands", WackenTheme.ButtonStyle.SECONDARY, view -> finish());
        screen.addView(back, fullWidthButtonLayout());

        try {
            repositories = new AppRepositories(this);
            requestScheduleLocks(repositories.scheduleLocks());
            addScheduleLockStatus(screen);
            SharedSchedule schedule = new GenerateSharedScheduleUseCase(
                    repositories.performances(),
                    repositories.ratings(),
                    repositories.distances()
            ).generate();
            addSchedule(screen, schedule);
        } catch (Exception error) {
            SupabaseDiagnostics.warn(
                    "schedule",
                    "generation_failed",
                    "screen=group_schedule user_message=" + ScheduleErrorMessage.userMessage(error),
                    error
            );
            screen.addView(message(ScheduleErrorMessage.generationFailure(error), COLOR_ACCENT));
        }

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(COLOR_BACKGROUND);
        scrollView.addView(screen);
        return scrollView;
    }

    private void requestScheduleLocks(ScheduleLockStore scheduleLocks) {
        if (scheduleLocksRequested) {
            return;
        }
        scheduleLocksRequested = true;
        new Thread(() -> {
            try {
                Map<String, String> locks = scheduleLocks.pullGroupLocks();
                runOnUiThread(() -> {
                    if (isFinishing()) {
                        return;
                    }
                    manualSelections = new ScheduleManualSelections(locks);
                    scheduleLocksLoaded = true;
                    scheduleLockWarning = null;
                    setContentView(render());
                });
            } catch (Exception error) {
                SupabaseDiagnostics.warn(
                        "schedule",
                        "lock_load_failed",
                        "screen=group_schedule user_message=" + ScheduleErrorMessage.userMessage(error),
                        error
                );
                runOnUiThread(() -> {
                    if (isFinishing()) {
                        return;
                    }
                    scheduleLocksLoaded = true;
                    scheduleLockWarning = ScheduleErrorMessage.lockLoadFailure(error);
                    setContentView(render());
                });
            }
        }, "schedule-lock-loader").start();
    }

    private void addScheduleLockStatus(LinearLayout screen) {
        if (scheduleLockWarning != null) {
            screen.addView(message(scheduleLockWarning, COLOR_AMBER));
        } else if (!scheduleLocksLoaded) {
            screen.addView(message("Locked schedule choices are syncing. Showing generated schedule.", COLOR_MUTED));
        }
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
        screen.addView(filterControls());
        screen.addView(scheduleLegend());
        java.util.Optional<ScheduleDay> selected = ScheduleDaySelection.selectedDay(schedule.days(), selectedScheduleDate);
        if (selected.isEmpty()) {
            screen.addView(message("No selected performances are available yet.", COLOR_MUTED));
            return;
        }
        selectedScheduleDate = selected.get().date();
        screen.addView(daySelector(schedule.days(), selected.get()));
        TextView dayTitle = sectionTitle(selected.get().date().format(DATE));
        screen.addView(dayTitle);
        screen.addView(dayCalendar(selected.get()));
    }

    private View daySelector(java.util.List<ScheduleDay> days, ScheduleDay selected) {
        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER_VERTICAL);
        buttons.setPadding(0, 0, 0, dp(8));
        DateTimeFormatter weekday = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
        for (ScheduleDay day : days) {
            boolean selectedDay = ScheduleDaySelection.isSelected(day, selected);
            Button button = WackenTheme.actionButton(
                    this,
                    day.date().format(weekday) + " " + day.date().getDayOfMonth(),
                    selectedDay ? WackenTheme.ButtonStyle.PRIMARY : WackenTheme.ButtonStyle.SECONDARY,
                    view -> {
                        selectedScheduleDate = day.date();
                        setContentView(render());
                    }
            );
            button.setTextColor(Color.WHITE);
            button.setTextSize(12);
            button.setPadding(dp(10), 0, dp(10), 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    dp(36)
            );
            params.setMargins(0, 0, dp(6), 0);
            buttons.addView(button, params);
        }
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.addView(buttons);
        return scroll;
    }

    private View filterControls() {
        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(dp(10), dp(8), dp(10), dp(8));
        controls.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, COLOR_GRID, 6));

        CheckBox hideBarredBox = new CheckBox(this);
        hideBarredBox.setText("Hide barred");
        hideBarredBox.setTextColor(COLOR_TEXT);
        hideBarredBox.setTextSize(13);
        hideBarredBox.setTypeface(Typeface.DEFAULT_BOLD);
        hideBarredBox.setChecked(hideBarred);
        hideBarredBox.setOnCheckedChangeListener((button, checked) -> {
            hideBarred = checked;
            setContentView(render());
        });
        controls.addView(hideBarredBox);
        TextView thresholdLabel = new TextView(this);
        thresholdLabel.setText("Hide <= ");
        thresholdLabel.setTextColor(COLOR_MUTED);
        thresholdLabel.setTextSize(12);
        thresholdLabel.setTypeface(Typeface.DEFAULT_BOLD);
        thresholdLabel.setPadding(dp(8), 0, 0, 0);
        controls.addView(thresholdLabel);
        controls.addView(thresholdButton("Off", 0));
        controls.addView(thresholdButton("1★", 1));
        controls.addView(thresholdButton("2★", 2));
        controls.addView(thresholdButton("3★", 3));
        controls.addView(thresholdButton("4★", 4));
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setPadding(0, dp(4), 0, dp(8));
        scroll.addView(controls);
        return scroll;
    }

    private Button thresholdButton(String label, int threshold) {
        Button button = WackenTheme.actionButton(
                this,
                label,
                selectedHideThreshold == threshold ? WackenTheme.ButtonStyle.PRIMARY : WackenTheme.ButtonStyle.SECONDARY,
                view -> {
                    selectedHideThreshold = threshold;
                    setContentView(render());
                }
        );
        button.setTextColor(Color.WHITE);
        button.setTextSize(11);
        button.setPadding(dp(8), 0, dp(8), 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(34)
        );
        params.setMargins(dp(4), 0, 0, 0);
        button.setLayoutParams(params);
        return button;
    }

    private View scheduleLegend() {
        LinearLayout legend = new LinearLayout(this);
        legend.setOrientation(LinearLayout.VERTICAL);
        legend.setPadding(dp(12), dp(10), dp(12), dp(10));
        legend.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, COLOR_GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layout.setMargins(0, 0, 0, dp(8));
        legend.setLayoutParams(layout);

        TextView title = detailText("Legend", COLOR_AMBER, 13, true);
        title.setPadding(0, 0, 0, dp(4));
        legend.addView(title);

        legend.addView(legendLine("Gold border", "5★ must see", WackenTheme.GOLD));
        legend.addView(legendLine("Red border", "4★ strong choice", WackenTheme.RED));
        legend.addView(legendLine("Grey border", "2-3★ optional", WackenTheme.STEEL_GREY));
        legend.addView(legendLine("Scratched", "lower-rated visible overlap to skip", WackenTheme.RED));
        legend.addView(legendLine("🔒", "locked group choice", COLOR_MUTED));
        legend.addView(legendLine("≡", "tie shown first in alternatives", COLOR_MUTED));
        legend.addView(legendLine("Filters", "hide barred acts or acts at/below selected stars", COLOR_MUTED));
        return legend;
    }

    private TextView legendLine(String label, String description, int accent) {
        TextView line = detailText(label + "  " + description, COLOR_TEXT, 11, false);
        line.setTextColor(accent == COLOR_MUTED ? COLOR_MUTED : COLOR_TEXT);
        line.setPadding(0, dp(1), 0, dp(1));
        return line;
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
        ScheduleRatingFilter filter = scheduleFilter();
        java.util.List<TimelineSlot> candidateSlots = new java.util.ArrayList<>();
        java.util.List<ScheduleDecisionCandidate> candidateViews = new java.util.ArrayList<>();
        for (TimelineSlot slot : day.slots()) {
            candidateSlots.add(slot);
            candidateViews.add(manualSelections.visibleCandidate(slot));
        }
        java.util.List<TimelineSlot> visibleSlots = new java.util.ArrayList<>();
        java.util.List<ScheduleDecisionCandidate> visibleCandidates = new java.util.ArrayList<>();
        for (int index = 0; index < candidateSlots.size(); index++) {
            ScheduleDecisionCandidate visible = candidateViews.get(index);
            if (filter.shows(visible, candidateViews)) {
                visibleSlots.add(candidateSlots.get(index));
                visibleCandidates.add(visible);
            }
        }
        if (visibleSlots.isEmpty()) {
            FrameLayout empty = new FrameLayout(this);
            empty.addView(message("No acts match the active filters.", COLOR_MUTED));
            return empty;
        }
        ScheduleCalendarLayout layout = ScheduleCalendarLayout.forCandidates(visibleCandidates, day.date());
        int calendarHeight = dp(TIME_HEADER_HEIGHT_DP + (layout.stageRowCount() * STAGE_ROW_HEIGHT_DP));
        int timelineWidth = dp(layout.hourCount() * HOUR_WIDTH_DP);
        LinearLayout schedule = new LinearLayout(this);
        schedule.setOrientation(LinearLayout.HORIZONTAL);
        schedule.setBackgroundColor(COLOR_BACKGROUND);

        FrameLayout stageLabels = new FrameLayout(this);
        stageLabels.setBackgroundColor(COLOR_BACKGROUND);
        stageLabels.setLayoutParams(new LinearLayout.LayoutParams(
                dp(STAGE_LABEL_WIDTH_DP),
                calendarHeight
        ));

        FrameLayout timeline = new FrameLayout(this);
        timeline.setBackgroundColor(COLOR_BACKGROUND);
        timeline.setLayoutParams(new FrameLayout.LayoutParams(
                timelineWidth,
                calendarHeight
        ));

        for (int row = 0; row < layout.stageRows().size(); row++) {
            stageLabels.addView(stageHeader(layout.stageRows().get(row)), stageHeaderLayout(row));
            timeline.addView(stageRowLine(), stageRowLineLayout(layout, row));
        }
        for (int hour = 0; hour <= layout.hourCount(); hour++) {
            timeline.addView(hourLabel(layout, hour), hourLabelLayout(hour));
            timeline.addView(hourGridLine(), hourGridLineLayout(layout, hour));
            if (hour < layout.hourCount()) {
                timeline.addView(halfHourNotch(), halfHourNotchLayout(hour));
                timeline.addView(halfHourGridLine(), halfHourGridLineLayout(layout, hour));
            }
        }
        for (int index = 0; index < visibleSlots.size(); index++) {
            TimelineSlot slot = visibleSlots.get(index);
            ScheduleDecisionCandidate visible = manualSelections.visibleCandidate(slot);
            timeline.addView(
                    slotView(slot, visible, visibleCandidates, layout.durationMinutes(visible)),
                    slotLayout(layout, visible)
            );
            if (index < visibleSlots.size() - 1) {
                ScheduleDecisionCandidate next = manualSelections.visibleCandidate(visibleSlots.get(index + 1));
                timeline.addView(walkingMarker(slot), walkingMarkerLayout(layout, visible, next));
            }
        }
        HorizontalScrollView horizontal = new HorizontalScrollView(this);
        horizontal.setHorizontalScrollBarEnabled(true);
        horizontal.setFillViewport(false);
        horizontal.setBackgroundColor(COLOR_BACKGROUND);
        horizontal.addView(timeline);
        schedule.addView(stageLabels);
        schedule.addView(horizontal, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                calendarHeight
        ));
        return schedule;
    }

    private TextView stageHeader(String stageName) {
        TextView header = new TextView(this);
        header.setText(stageName);
        header.setTextColor(COLOR_AMBER);
        header.setTextSize(13);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        header.setSingleLine(false);
        header.setEllipsize(TextUtils.TruncateAt.END);
        header.setPadding(0, 0, dp(10), 0);
        return header;
    }

    private FrameLayout.LayoutParams stageHeaderLayout(int row) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(STAGE_LABEL_WIDTH_DP),
                dp(STAGE_ROW_HEIGHT_DP - ROW_GAP_DP)
        );
        params.topMargin = dp(TIME_HEADER_HEIGHT_DP + (row * STAGE_ROW_HEIGHT_DP));
        return params;
    }

    private TextView hourLabel(ScheduleCalendarLayout layout, int hourOffset) {
        TextView label = new TextView(this);
        label.setText(layout.hourLabel(hourOffset));
        label.setTextColor(COLOR_GRID);
        label.setTextSize(11);
        label.setSingleLine(true);
        return label;
    }

    private FrameLayout.LayoutParams hourLabelLayout(int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(64),
                dp(TIME_HEADER_HEIGHT_DP)
        );
        params.leftMargin = dp(Math.max(0, (hourOffset * HOUR_WIDTH_DP) - 28));
        params.topMargin = 0;
        return params;
    }

    private View hourGridLine() {
        View line = new View(this);
        line.setBackgroundColor(COLOR_GRID);
        return line;
    }

    private FrameLayout.LayoutParams hourGridLineLayout(ScheduleCalendarLayout layout, int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(1),
                stageGridHeight(layout)
        );
        params.leftMargin = dp(hourOffset * HOUR_WIDTH_DP);
        params.topMargin = dp(TIME_HEADER_HEIGHT_DP);
        return params;
    }

    private View halfHourNotch() {
        View line = new View(this);
        line.setBackgroundColor(COLOR_GRID);
        return line;
    }

    private FrameLayout.LayoutParams halfHourNotchLayout(int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(1),
                dp(12)
        );
        params.leftMargin = halfHourLeftMargin(hourOffset);
        params.topMargin = dp(TIME_HEADER_HEIGHT_DP - 12);
        return params;
    }

    private View halfHourGridLine() {
        return new VerticalDottedLineView(this, COLOR_GRID);
    }

    private FrameLayout.LayoutParams halfHourGridLineLayout(ScheduleCalendarLayout layout, int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(8),
                stageGridHeight(layout)
        );
        params.leftMargin = halfHourLeftMargin(hourOffset) - dp(4);
        params.topMargin = dp(TIME_HEADER_HEIGHT_DP);
        return params;
    }

    private int halfHourLeftMargin(int hourOffset) {
        return dp((hourOffset * HOUR_WIDTH_DP) + (HOUR_WIDTH_DP / 2));
    }

    private int stageGridHeight(ScheduleCalendarLayout layout) {
        return dp(layout.stageRowCount() * STAGE_ROW_HEIGHT_DP);
    }

    private View stageRowLine() {
        View line = new View(this);
        line.setBackgroundColor(WackenTheme.GRID);
        return line;
    }

    private FrameLayout.LayoutParams stageRowLineLayout(ScheduleCalendarLayout layout, int row) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(layout.hourCount() * HOUR_WIDTH_DP),
                dp(1)
        );
        params.leftMargin = 0;
        params.topMargin = dp(TIME_HEADER_HEIGHT_DP + (row * STAGE_ROW_HEIGHT_DP));
        return params;
    }

    private FrameLayout.LayoutParams slotLayout(ScheduleCalendarLayout layout, ScheduleDecisionCandidate candidate) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(Math.max(88, layout.durationMinutes(candidate) * HOUR_WIDTH_DP / 60)),
                dp(STAGE_ROW_HEIGHT_DP - ROW_GAP_DP)
        );
        params.leftMargin = dp(layout.leftOffsetMinutes(candidate) * HOUR_WIDTH_DP / 60);
        params.topMargin = dp(TIME_HEADER_HEIGHT_DP + (layout.stageRowIndex(candidate) * STAGE_ROW_HEIGHT_DP) + 6);
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
        marker.setPadding(dp(4), 0, dp(4), 0);
        marker.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, COLOR_AMBER, 4));
        return marker;
    }

    private FrameLayout.LayoutParams walkingMarkerLayout(
            ScheduleCalendarLayout layout,
            ScheduleDecisionCandidate from,
            ScheduleDecisionCandidate to
    ) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(72),
                dp(22)
        );
        params.leftMargin = dp(Math.max(0, (layout.walkingMarkerOffsetMinutes(from, to) * HOUR_WIDTH_DP / 60) - 36));
        params.topMargin = dp(TIME_HEADER_HEIGHT_DP
                + Math.max(0, (Math.min(layout.stageRowIndex(from), layout.stageRowIndex(to)) * STAGE_ROW_HEIGHT_DP) - 2));
        return params;
    }

    private LinearLayout slotView(
            TimelineSlot slot,
            ScheduleDecisionCandidate visible,
            java.util.List<ScheduleDecisionCandidate> visibleCandidates,
            int blockMinutes
    ) {
        ScheduleBlockContent content = ScheduleBlockContent.from(slot, visible, blockMinutes);
        ScheduleBlockStyle style = ScheduleBlockStyle.from(visible, visibleCandidates);
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackground(slotBackground(style));
        panel.setPadding(dp(10), dp(6), dp(10), dp(6));
        panel.setClickable(true);
        panel.setOnClickListener(view -> showDecisionDetails(slot));

        TextView timeRange = new TextView(this);
        timeRange.setText(content.timeRangeLine());
        timeRange.setTextColor(COLOR_MUTED);
        timeRange.setTextSize(10);
        timeRange.setTypeface(Typeface.DEFAULT_BOLD);
        timeRange.setSingleLine(true);
        panel.addView(timeRange);

        TextView band = new TextView(this);
        band.setText(content.bandLine());
        band.setTextColor(COLOR_TEXT);
        band.setTextSize(16);
        band.setTypeface(Typeface.DEFAULT_BOLD);
        band.setSingleLine(true);
        band.setEllipsize(TextUtils.TruncateAt.END);
        panel.addView(band);

        content.lostAlternativeLine().ifPresent(lost -> {
            TextView alternative = new TextView(this);
            alternative.setText(lost);
            alternative.setTextColor(COLOR_MUTED);
            alternative.setTextSize(11);
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

        TextView title = detailText(ScheduleBandDisplayName.clean(slot.bandName()), COLOR_TEXT, 24, true);
        title.setTextColor(COLOR_AMBER);
        title.setPadding(0, 0, 0, dp(14));
        detail.addView(title);

        detail.addView(detailSection("Chosen act"));
        final AlertDialog[] dialog = new AlertDialog[1];
        java.util.List<ScheduleDecisionCandidate> candidates = scheduleFilter()
                .visibleCandidates(manualSelections.detailCandidates(slot));
        if (candidates.isEmpty()) {
            detail.addView(detailText("No acts match the active filters.", COLOR_MUTED, 14, false));
            return;
        }
        detail.addView(candidateView(slot, candidates.get(0), candidates, () -> dialog[0].dismiss()));

        detail.addView(detailSection("Alternatives"));
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
        Button close = WackenTheme.actionButton(this, "Close", WackenTheme.ButtonStyle.SECONDARY, view -> dialog[0].dismiss());
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
        panel.setPadding(dp(10), dp(8), dp(10), dp(10));
        panel.setBackground(WackenTheme.panelBackground(
                this,
                candidate.selected() ? WackenTheme.ELEVATED_PANEL : WackenTheme.PANEL,
                candidate.selected() ? COLOR_ACCENT : COLOR_GRID,
                6
        ));
        LinearLayout.LayoutParams panelLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        panelLayout.setMargins(0, dp(6), 0, dp(8));
        panel.setLayoutParams(panelLayout);

        TextView name = detailText(ScheduleBandDisplayName.clean(candidate.bandName()) + " " + stars(candidate.rating()),
                candidate.selected() ? COLOR_TEXT : COLOR_MUTED,
                16,
                candidate.selected());
        name.setClickable(true);
        name.setOnClickListener(view -> {
            openBandDetail(candidate);
            afterSelect.run();
        });
        panel.addView(name);
        if (candidate.hasPersonRatings()) {
            TextView personRatings = detailText(
                    candidate.personRatingSummary(),
                    COLOR_MUTED,
                    12,
                    false
            );
            personRatings.setPadding(0, dp(2), 0, dp(2));
            panel.addView(personRatings);
        }

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
            Button select = WackenTheme.actionButton(this, "Select as act", WackenTheme.ButtonStyle.PRIMARY, null);
            select.setOnClickListener(view -> {
                try {
                    repositories.scheduleLocks().saveGroupLock(
                            ScheduleManualSelections.conflictKey(slot),
                            ScheduleManualSelections.candidateKey(candidate)
                    );
                    manualSelections.select(slot, candidate);
                    setContentView(render());
                    afterSelect.run();
                } catch (IOException error) {
                    showError("Could not lock schedule choice: " + error.getMessage());
                }
            });
            panel.addView(select);
        } else if (manualSelections.isManual(slot)) {
            Button unlock = WackenTheme.actionButton(this, "Unlock generated choice", WackenTheme.ButtonStyle.SECONDARY, null);
            unlock.setOnClickListener(view -> {
                try {
                    repositories.scheduleLocks().clearGroupLock(ScheduleManualSelections.conflictKey(slot));
                    manualSelections.clear(slot);
                    setContentView(render());
                    afterSelect.run();
                } catch (IOException error) {
                    showError("Could not unlock schedule choice: " + error.getMessage());
                }
            });
            panel.addView(unlock);
        }
        return panel;
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
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
            details.add(minutes + " min to " + ScheduleBandDisplayName.clean(other.bandName()));
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

    private TextView detailSection(String text) {
        TextView section = detailText(text, COLOR_ACCENT, 12, true);
        section.setPadding(0, dp(10), 0, dp(4));
        return section;
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

    private Drawable slotBackground(ScheduleBlockStyle style) {
        int borderColor = style.borderColor();
        int scratchColor = scratchColor(borderColor);
        return new ScheduleBlockDrawable(
                style.fillColor(),
                borderColor,
                scratchColor,
                style.scratched(),
                dp(4),
                dp(1),
                dp(14),
                dp(34)
        );
    }

    private int scratchColor(int borderColor) {
        return Color.argb(
                96,
                Math.min(255, Color.red(borderColor) + 80),
                Math.min(255, Color.green(borderColor) + 80),
                Math.min(255, Color.blue(borderColor) + 80)
        );
    }

    private String stars(int rating) {
        int safeRating = Math.max(0, Math.min(5, rating));
        StringBuilder text = new StringBuilder(5);
        for (int index = 0; index < 5; index++) {
            text.append(index < safeRating ? "★" : "☆");
        }
        return text.toString();
    }

    private ScheduleRatingFilter scheduleFilter() {
        if (hideBarred) {
            return ScheduleRatingFilter.hideBarred(selectedHideThreshold);
        }
        return selectedHideThreshold > 0
                ? ScheduleRatingFilter.hideAtOrBelow(selectedHideThreshold)
                : ScheduleRatingFilter.none();
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

    private static final class VerticalDottedLineView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        VerticalDottedLineView(Context context, int color) {
            super(context);
            float density = context.getResources().getDisplayMetrics().density;
            paint.setColor(color);
            paint.setStrokeWidth(density);
            paint.setStyle(Paint.Style.STROKE);
            paint.setPathEffect(new DashPathEffect(new float[]{3f * density, 8f * density}, 0f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float x = getWidth() / 2f;
            canvas.drawLine(x, 0f, x, getHeight(), paint);
        }
    }

    private static final class ScheduleBlockDrawable extends Drawable {
        private final Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint scratchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final boolean scratched;
        private final float radius;
        private final float scratchGap;
        private final RectF bounds = new RectF();

        ScheduleBlockDrawable(
                int fillColor,
                int borderColor,
                int scratchColor,
                boolean scratched,
                int radius,
                int borderWidth,
                int scratchWidth,
                int scratchGap
        ) {
            this.scratched = scratched;
            this.radius = radius;
            this.scratchGap = scratchGap;
            fillPaint.setColor(fillColor);
            fillPaint.setStyle(Paint.Style.FILL);
            borderPaint.setColor(borderColor);
            borderPaint.setStyle(Paint.Style.STROKE);
            borderPaint.setStrokeWidth(borderWidth);
            scratchPaint.setColor(scratchColor);
            scratchPaint.setStyle(Paint.Style.STROKE);
            scratchPaint.setStrokeWidth(scratchWidth);
            scratchPaint.setStrokeCap(Paint.Cap.SQUARE);
        }

        @Override
        public void draw(Canvas canvas) {
            bounds.set(getBounds());
            float halfStroke = borderPaint.getStrokeWidth() / 2f;
            bounds.inset(halfStroke, halfStroke);
            canvas.drawRoundRect(bounds, radius, radius, fillPaint);
            if (scratched) {
                int save = canvas.save();
                canvas.clipRect(bounds);
                float start = -bounds.height();
                float end = bounds.width() + bounds.height();
                for (float x = start; x < end; x += scratchGap) {
                    canvas.drawLine(x, bounds.bottom, x + bounds.height(), bounds.top, scratchPaint);
                }
                canvas.restoreToCount(save);
            }
            canvas.drawRoundRect(bounds, radius, radius, borderPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            fillPaint.setAlpha(alpha);
            borderPaint.setAlpha(alpha);
            scratchPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(android.graphics.ColorFilter colorFilter) {
            fillPaint.setColorFilter(colorFilter);
            borderPaint.setColorFilter(colorFilter);
            scratchPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
