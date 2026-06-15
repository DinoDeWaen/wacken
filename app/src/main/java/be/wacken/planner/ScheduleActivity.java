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
    private boolean hideTwoStarOrLower;
    private int selectedHideThreshold;

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
        screen.addView(filterControls());
        for (ScheduleDay day : schedule.days()) {
            TextView dayTitle = sectionTitle(day.date().format(DATE));
            screen.addView(dayTitle);
            screen.addView(dayCalendar(day));
        }
    }

    private View filterControls() {
        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0, dp(6), 0, dp(8));

        CheckBox hideWeak = new CheckBox(this);
        hideWeak.setText("Hide <=2★");
        hideWeak.setTextColor(COLOR_TEXT);
        hideWeak.setTextSize(13);
        hideWeak.setTypeface(Typeface.DEFAULT_BOLD);
        hideWeak.setChecked(hideTwoStarOrLower);
        hideWeak.setOnCheckedChangeListener((button, checked) -> {
            hideTwoStarOrLower = checked;
            setContentView(render());
        });
        controls.addView(hideWeak);
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
        scroll.addView(controls);
        return scroll;
    }

    private Button thresholdButton(String label, int threshold) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setTextSize(11);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setMinWidth(0);
        button.setMinHeight(0);
        button.setPadding(dp(8), 0, dp(8), 0);
        button.setBackgroundColor(selectedHideThreshold == threshold ? COLOR_ACCENT : Color.rgb(49, 56, 58));
        button.setOnClickListener(view -> {
            selectedHideThreshold = threshold;
            setContentView(render());
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                dp(34)
        );
        params.setMargins(dp(4), 0, 0, 0);
        button.setLayoutParams(params);
        return button;
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
        java.util.List<TimelineSlot> visibleSlots = new java.util.ArrayList<>();
        java.util.List<ScheduleDecisionCandidate> visibleCandidates = new java.util.ArrayList<>();
        for (TimelineSlot slot : day.slots()) {
            ScheduleDecisionCandidate visible = manualSelections.visibleCandidate(slot);
            if (filter.shows(visible)) {
                visibleSlots.add(slot);
                visibleCandidates.add(visible);
            }
        }
        if (visibleSlots.isEmpty()) {
            FrameLayout empty = new FrameLayout(this);
            empty.addView(message("No acts match the active filters.", COLOR_MUTED));
            return empty;
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
            calendar.addView(hourLabel(layout, hour), hourLabelLayout(hour));
            calendar.addView(hourGridLine(), hourGridLineLayout(layout, hour));
            if (hour < layout.hourCount()) {
                calendar.addView(halfHourNotch(), halfHourNotchLayout(hour));
                calendar.addView(halfHourGridLine(), halfHourGridLineLayout(layout, hour));
            }
        }
        for (int index = 0; index < visibleSlots.size(); index++) {
            TimelineSlot slot = visibleSlots.get(index);
            ScheduleDecisionCandidate visible = manualSelections.visibleCandidate(slot);
            calendar.addView(
                    slotView(slot, visible, visibleCandidates, layout.durationMinutes(visible)),
                    slotLayout(layout, visible)
            );
            if (index < visibleSlots.size() - 1) {
                ScheduleDecisionCandidate next = manualSelections.visibleCandidate(visibleSlots.get(index + 1));
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
                dp(TIME_LABEL_WIDTH_DP),
                dp(18)
        );
        params.topMargin = dp(STAGE_HEADER_HEIGHT_DP + (hourOffset * HOUR_HEIGHT_DP));
        return params;
    }

    private View hourGridLine() {
        View line = new View(this);
        line.setBackgroundColor(COLOR_GRID);
        return line;
    }

    private FrameLayout.LayoutParams hourGridLineLayout(ScheduleCalendarLayout layout, int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                stageGridWidth(layout),
                dp(1)
        );
        params.leftMargin = dp(TIME_LABEL_WIDTH_DP);
        params.topMargin = dp(STAGE_HEADER_HEIGHT_DP + (hourOffset * HOUR_HEIGHT_DP) + 9);
        return params;
    }

    private View halfHourNotch() {
        View line = new View(this);
        line.setBackgroundColor(COLOR_GRID);
        return line;
    }

    private FrameLayout.LayoutParams halfHourNotchLayout(int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                dp(12),
                dp(1)
        );
        params.leftMargin = dp(TIME_LABEL_WIDTH_DP - 16);
        params.topMargin = halfHourTopMargin(hourOffset);
        return params;
    }

    private View halfHourGridLine() {
        return new DottedLineView(this, COLOR_GRID);
    }

    private FrameLayout.LayoutParams halfHourGridLineLayout(ScheduleCalendarLayout layout, int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                stageGridWidth(layout),
                dp(8)
        );
        params.leftMargin = dp(TIME_LABEL_WIDTH_DP);
        params.topMargin = halfHourTopMargin(hourOffset) - dp(4);
        return params;
    }

    private int halfHourTopMargin(int hourOffset) {
        return dp(STAGE_HEADER_HEIGHT_DP + (hourOffset * HOUR_HEIGHT_DP) + (HOUR_HEIGHT_DP / 2) + 9);
    }

    private int stageGridWidth(ScheduleCalendarLayout layout) {
        return dp(layout.stageColumnCount() * STAGE_COLUMN_WIDTH_DP);
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
        title.setPadding(0, 0, 0, dp(14));
        detail.addView(title);

        detail.addView(detailText("Chosen act", COLOR_ACCENT, 12, true));
        final AlertDialog[] dialog = new AlertDialog[1];
        java.util.List<ScheduleDecisionCandidate> candidates = scheduleFilter()
                .visibleCandidates(manualSelections.detailCandidates(slot));
        if (candidates.isEmpty()) {
            detail.addView(detailText("No acts match the active filters.", COLOR_MUTED, 14, false));
            return;
        }
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
        int threshold = Math.max(hideTwoStarOrLower ? 2 : 0, selectedHideThreshold);
        return threshold > 0 ? ScheduleRatingFilter.hideAtOrBelow(threshold) : ScheduleRatingFilter.none();
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

    private static final class DottedLineView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        DottedLineView(Context context, int color) {
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
            float y = getHeight() / 2f;
            canvas.drawLine(0f, y, getWidth(), y, paint);
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
