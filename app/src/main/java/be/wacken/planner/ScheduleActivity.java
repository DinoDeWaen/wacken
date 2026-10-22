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
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.ViewGroup;
import android.content.res.ColorStateList;
import android.graphics.drawable.StateListDrawable;
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
    private boolean scheduleKeyVisible;
    private int calendarX,outerY,focusId=View.NO_ID,viewportGeneration;
    private boolean restoringViewport;
    private String focusTag,decisionKey;
    private int decisionY;
    private AlertDialog decisionDialog;
    private ScrollView decisionScroll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppearanceController.attach(this);
        if(savedInstanceState!=null) {
            hideBarred=savedInstanceState.getBoolean("hideBarred");
            selectedHideThreshold=Math.max(0,Math.min(4,savedInstanceState.getInt("threshold")));
            scheduleKeyVisible=savedInstanceState.getBoolean("keyVisible");
            String date=savedInstanceState.getString("scheduleDate");
            if(date!=null)try{selectedScheduleDate=LocalDate.parse(date);}catch(java.time.format.DateTimeParseException ignored) { }
            calendarX=savedInstanceState.getInt("calendarX");outerY=savedInstanceState.getInt("outerY");
            focusId=savedInstanceState.getInt("focusId",View.NO_ID);focusTag=savedInstanceState.getString("focusTag");
            decisionKey=savedInstanceState.getString("decisionKey");decisionY=savedInstanceState.getInt("decisionY");
        }
        rerender();
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        captureViewport();
        state.putBoolean("hideBarred",hideBarred);state.putInt("threshold",selectedHideThreshold);
        state.putBoolean("keyVisible",scheduleKeyVisible);
        if(selectedScheduleDate!=null)state.putString("scheduleDate",selectedScheduleDate.toString());
        state.putInt("calendarX",calendarX);state.putInt("outerY",outerY);
        state.putInt("focusId",focusId);state.putString("focusTag",focusTag);
        state.putString("decisionKey",decisionKey);
        state.putInt("decisionY",decisionScroll==null?decisionY:decisionScroll.getScrollY());
        super.onSaveInstanceState(state);
    }

    @Override protected void onDestroy() {
        if(decisionDialog!=null)decisionDialog.dismiss();
        super.onDestroy();
    }

    private void captureViewport() {
        if(restoringViewport)return;
        ScrollView scroll=findViewById(R.id.destination_content);
        HorizontalScrollView calendar=scroll==null?null:scroll.findViewWithTag("schedule-calendar");
        if(calendar!=null){calendarX=calendar.getScrollX();outerY=scroll.getScrollY();}
        View focus=getCurrentFocus();
        if(focus!=null) {
            focusId=focus.getId();
            focusTag=focus.getTag() instanceof String tag?tag:null;
        }
    }

    private void rerender() {
        captureViewport();
        if(decisionDialog!=null) {
            decisionY=decisionScroll==null?decisionY:decisionScroll.getScrollY();
            AlertDialog previous=decisionDialog;
            decisionDialog=null;decisionScroll=null;
            previous.dismiss();
        }
        setContentView(render());
    }

    private void restoreDecision(SharedSchedule schedule) {
        if(decisionKey==null||decisionDialog!=null||!scheduleLocksLoaded||isFinishing()||isDestroyed())return;
        for(ScheduleDay day:schedule.days())for(TimelineSlot slot:day.slots())
            if(decisionKey.equals(ScheduleManualSelections.conflictKey(slot))){showDecisionDetails(slot);return;}
        decisionKey=null;decisionY=0;
        showError("This schedule decision is no longer available. Choose a current act to review.");
    }

    private void restoreViewport(ScrollView scroll) {
        int generation=++viewportGeneration;
        restoringViewport=true;
        scroll.post(()->{
            if(generation!=viewportGeneration||isFinishing()||isDestroyed())return;
            HorizontalScrollView calendar=scroll.findViewWithTag("schedule-calendar");
            if(calendar!=null)calendar.scrollTo(calendarX,0);
            scroll.scrollTo(0,outerY);
            if(!scroll.isInTouchMode()) {
                View focus=focusId==View.NO_ID?null:scroll.findViewById(focusId);
                if(focus==null&&focusTag!=null)focus=scroll.findViewWithTag(focusTag);
                if(focus!=null)focus.requestFocus();
            }
            restoringViewport=false;
        });
    }

    private View render() {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        WackenTheme.backgroundColor(screen, COLOR_BACKGROUND);
        int padding = dp(16);
        screen.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText("Group Schedule");
        WackenTheme.textColor(title, COLOR_AMBER);
        title.setTextSize(28);
        WackenTheme.heading(title);
        title.setGravity(Gravity.START);
        screen.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Your group’s actual acts, alternatives and walking context");
        WackenTheme.textColor(subtitle, COLOR_MUTED);
        subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
        subtitle.setPadding(0, dp(4), 0, dp(16));
        screen.addView(subtitle);

        Button back = WackenTheme.actionButton(this, "Back to bands", WackenTheme.ButtonStyle.SECONDARY,
                view -> MainNavigation.open(this,MainNavigation.Destination.BANDS));


        try {
            repositories = new AppRepositories(this);
            requestScheduleLocks(repositories.scheduleLocks());
            SharedSchedule schedule = new GenerateSharedScheduleUseCase(
                    repositories.performances(),
                    repositories.ratings(),
                    repositories.distances()
            ).generate();
            addSchedule(screen, schedule);
            addScheduleLockStatus(screen);
            screen.addView(message("Device sync: "+repositories.pendingSyncSummary().description(),COLOR_MUTED));
            screen.post(()->restoreDecision(schedule));
        } catch (Exception error) {
            SupabaseDiagnostics.warn(
                    "schedule",
                    "generation_failed",
                    "screen=group_schedule user_message=" + ScheduleErrorMessage.userMessage(error),
                    error
            );
            screen.addView(message(ScheduleErrorMessage.generationFailure(error), COLOR_ACCENT));
        }

        screen.addView(back,fullWidthButtonLayout());
        ScrollView scrollView = new ScrollView(this);
        WackenTheme.backgroundColor(scrollView, COLOR_BACKGROUND);
        scrollView.addView(screen);
        restoreViewport(scrollView);
        return MainNavigation.wrap(this,scrollView,MainNavigation.Destination.SCHEDULE);
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
                    if (isFinishing()||isDestroyed()) {
                        return;
                    }
                    manualSelections = new ScheduleManualSelections(locks);
                    scheduleLocksLoaded = true;
                    scheduleLockWarning = null;
                    rerender();
                });
            } catch (Exception error) {
                SupabaseDiagnostics.warn(
                        "schedule",
                        "lock_load_failed",
                        "screen=group_schedule user_message=" + ScheduleErrorMessage.userMessage(error),
                        error
                );
                runOnUiThread(() -> {
                    if (isFinishing()||isDestroyed()) {
                        return;
                    }
                    scheduleLocksLoaded = true;
                    scheduleLockWarning = ScheduleErrorMessage.lockLoadFailure(error);
                    rerender();
                });
            }
        }, "schedule-lock-loader").start();
    }

    private void addScheduleLockStatus(LinearLayout screen) {
        if (scheduleLockWarning != null) {
            screen.addView(message(scheduleLockWarning, COLOR_AMBER));
            screen.addView(WackenTheme.actionButton(this,"Retry schedule sync",WackenTheme.ButtonStyle.SECONDARY,view->{
                scheduleLocksRequested=false;scheduleLocksLoaded=false;scheduleLockWarning=null;rerender();
            }));
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
        screen.addView(scheduleKeyToggle());
        if (scheduleKeyVisible) {
            screen.addView(scheduleLegend());
        }
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

    private View daySelector(java.util.List<ScheduleDay> days,ScheduleDay selected) {
        Spinner choice=selector("Festival day",days.stream().map(day->day.date().format(DATE)).toList(),
                days.indexOf(selected),position->{
                    LocalDate next=days.get(position).date();
                    if(!next.equals(selectedScheduleDate)){selectedScheduleDate=next;rerender();}
                });
        choice.setId(R.id.schedule_day);
        return choice;
    }

    private View filterControls() {
        LinearLayout controls=new LinearLayout(this);
        controls.setOrientation(LinearLayout.VERTICAL);
        controls.setPadding(dp(8),dp(8),dp(8),dp(8));
        CheckBox barred=new CheckBox(this);
        barred.setId(R.id.schedule_hide_barred);
        barred.setText(ScheduleLegendContent.hideBarredLabel());
        barred.setContentDescription("Hide barred overlaps");
        barred.setTextSize(16);barred.setMinHeight(dp(48));barred.setMinimumWidth(dp(48));
        WackenTheme.textColor(barred,COLOR_TEXT);WackenTheme.focusablePanel(barred);
        AppearanceController.bind(barred,"checkTint",(view,palette)->((CheckBox)view).setButtonTintList(ColorStateList.valueOf(palette.accent())));
        barred.setChecked(hideBarred);
        barred.setOnCheckedChangeListener((button,checked)->{hideBarred=checked;rerender();});
        controls.addView(barred,new LinearLayout.LayoutParams(-1,-2));
        controls.addView(detailText(ScheduleLegendContent.ratingThresholdLabel(),COLOR_TEXT,14,true));
        Spinner threshold=selector("Hide ratings at or below",java.util.List.of("Off","1★","2★","3★","4★"),
                selectedHideThreshold,position->{if(position!=selectedHideThreshold){selectedHideThreshold=position;rerender();}});
        threshold.setId(R.id.schedule_threshold);
        controls.addView(threshold,new LinearLayout.LayoutParams(-1,-2));
        return controls;
    }

    private Spinner selector(String label,java.util.List<String> labels,int selected,java.util.function.IntConsumer choose) {
        Spinner spinner=new Spinner(this);
        spinner.setContentDescription(label);spinner.setPrompt(label);spinner.setSaveEnabled(false);
        spinner.setMinimumHeight(dp(48));spinner.setMinimumWidth(dp(48));
        spinner.setPadding(dp(8),0,dp(8),0);
        WackenTheme.focusablePanel(spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,labels) {
            @Override public View getView(int position,View old,ViewGroup parent) {
                TextView text=(TextView)super.getView(position,old,parent);
                selectorText(text);text.setText(getItem(position)+" ▾");return text;
            }
            @Override public View getDropDownView(int position,View old,ViewGroup parent) {
                TextView text=(TextView)super.getDropDownView(position,old,parent);
                selectorText(text);return text;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);spinner.setSelection(Math.max(0,selected),false);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent,View view,int position,long id){choose.accept(position);}
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
        spinner.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        return spinner;
    }

    private void selectorText(TextView text) {
        text.setTextSize(16);text.setSingleLine(false);text.setMaxLines(Integer.MAX_VALUE);text.setEllipsize(null);
        text.setMinHeight(dp(48));text.setPadding(dp(8),dp(8),dp(8),dp(8));
        WackenTheme.textColor(text,COLOR_TEXT);WackenTheme.backgroundColor(text,WackenTheme.PANEL);
        if(text instanceof android.widget.CheckedTextView checked)
            AppearanceController.bind(checked,"checkTint",(view,palette)->((android.widget.CheckedTextView)view).setCheckMarkTintList(ColorStateList.valueOf(palette.accent())));
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

        TextView meaning=detailText(ScheduleLegendContent.description(),COLOR_TEXT,14,false);
        meaning.setLineSpacing(dp(4),1f);legend.addView(meaning);
        return legend;
    }

    private View scheduleKeyToggle() {
        Button toggle=WackenTheme.actionButton(this,scheduleKeyVisible?"Hide schedule key":"Show schedule key",
                WackenTheme.ButtonStyle.SECONDARY,view->{scheduleKeyVisible=!scheduleKeyVisible;rerender();});
        toggle.setId(R.id.schedule_key);toggle.setSelected(scheduleKeyVisible);toggle.setTextSize(16);
        toggle.setLayoutParams(fullWidthButtonLayout());
        return toggle;
    }

    private TextView sectionTitle(String text) {
        TextView title = new TextView(this);
        title.setText(text);
        WackenTheme.textColor(title, COLOR_AMBER);
        title.setTextSize(18);
        WackenTheme.heading(title);
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
        int calendarHeight = calendarDp(TIME_HEADER_HEIGHT_DP + (layout.stageRowCount() * STAGE_ROW_HEIGHT_DP));
        int timelineWidth = calendarDp(layout.hourCount() * HOUR_WIDTH_DP);
        LinearLayout schedule = new LinearLayout(this);
        schedule.setOrientation(LinearLayout.HORIZONTAL);
        WackenTheme.backgroundColor(schedule, COLOR_BACKGROUND);

        FrameLayout stageLabels = new FrameLayout(this);
        WackenTheme.backgroundColor(stageLabels, COLOR_BACKGROUND);
        stageLabels.setLayoutParams(new LinearLayout.LayoutParams(
                stageLabelWidth(),
                calendarHeight
        ));

        FrameLayout timeline = new FrameLayout(this);
        WackenTheme.backgroundColor(timeline, COLOR_BACKGROUND);
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
        horizontal.setTag("schedule-calendar");
        horizontal.setHorizontalScrollBarEnabled(true);
        horizontal.setFillViewport(false);
        WackenTheme.backgroundColor(horizontal, COLOR_BACKGROUND);
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
        WackenTheme.textColor(header, COLOR_AMBER);
        header.setTextSize(14);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        header.setSingleLine(false);
        header.setEllipsize(null);
        header.setPadding(0, 0, calendarDp(10), 0);
        return header;
    }

    private FrameLayout.LayoutParams stageHeaderLayout(int row) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                stageLabelWidth(),
                calendarDp(STAGE_ROW_HEIGHT_DP - ROW_GAP_DP)
        );
        params.topMargin = calendarDp(TIME_HEADER_HEIGHT_DP + (row * STAGE_ROW_HEIGHT_DP));
        return params;
    }

    private TextView hourLabel(ScheduleCalendarLayout layout, int hourOffset) {
        TextView label = new TextView(this);
        label.setText(layout.hourLabel(hourOffset));
        WackenTheme.textColor(label, COLOR_MUTED);
        label.setTextSize(14);
        label.setSingleLine(true);
        return label;
    }

    private FrameLayout.LayoutParams hourLabelLayout(int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                calendarDp(64),
                calendarDp(TIME_HEADER_HEIGHT_DP)
        );
        params.leftMargin = calendarDp(Math.max(0, (hourOffset * HOUR_WIDTH_DP) - 28));
        params.topMargin = 0;
        return params;
    }

    private View hourGridLine() {
        View line = new View(this);
        WackenTheme.backgroundColor(line, COLOR_GRID);
        return line;
    }

    private FrameLayout.LayoutParams hourGridLineLayout(ScheduleCalendarLayout layout, int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                calendarDp(1),
                stageGridHeight(layout)
        );
        params.leftMargin = calendarDp(hourOffset * HOUR_WIDTH_DP);
        params.topMargin = calendarDp(TIME_HEADER_HEIGHT_DP);
        return params;
    }

    private View halfHourNotch() {
        View line = new View(this);
        WackenTheme.backgroundColor(line, COLOR_GRID);
        return line;
    }

    private FrameLayout.LayoutParams halfHourNotchLayout(int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                calendarDp(1),
                calendarDp(12)
        );
        params.leftMargin = halfHourLeftMargin(hourOffset);
        params.topMargin = calendarDp(TIME_HEADER_HEIGHT_DP - 12);
        return params;
    }

    private View halfHourGridLine() {
        return new VerticalDottedLineView(this, COLOR_GRID);
    }

    private FrameLayout.LayoutParams halfHourGridLineLayout(ScheduleCalendarLayout layout, int hourOffset) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                calendarDp(8),
                stageGridHeight(layout)
        );
        params.leftMargin = halfHourLeftMargin(hourOffset) - calendarDp(4);
        params.topMargin = calendarDp(TIME_HEADER_HEIGHT_DP);
        return params;
    }

    private int halfHourLeftMargin(int hourOffset) {
        return calendarDp((hourOffset * HOUR_WIDTH_DP) + (HOUR_WIDTH_DP / 2));
    }

    private int stageGridHeight(ScheduleCalendarLayout layout) {
        return calendarDp(layout.stageRowCount() * STAGE_ROW_HEIGHT_DP);
    }

    private View stageRowLine() {
        View line = new View(this);
        WackenTheme.backgroundColor(line, WackenTheme.GRID);
        return line;
    }

    private FrameLayout.LayoutParams stageRowLineLayout(ScheduleCalendarLayout layout, int row) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                calendarDp(layout.hourCount() * HOUR_WIDTH_DP),
                calendarDp(1)
        );
        params.leftMargin = 0;
        params.topMargin = calendarDp(TIME_HEADER_HEIGHT_DP + (row * STAGE_ROW_HEIGHT_DP));
        return params;
    }

    private FrameLayout.LayoutParams slotLayout(ScheduleCalendarLayout layout, ScheduleDecisionCandidate candidate) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                calendarDp(Math.max(88, layout.durationMinutes(candidate) * HOUR_WIDTH_DP / 60)),
                calendarDp(STAGE_ROW_HEIGHT_DP - ROW_GAP_DP)
        );
        params.leftMargin = calendarDp(layout.leftOffsetMinutes(candidate) * HOUR_WIDTH_DP / 60);
        params.topMargin = calendarDp(TIME_HEADER_HEIGHT_DP + (layout.stageRowIndex(candidate) * STAGE_ROW_HEIGHT_DP) + 6);
        return params;
    }

    private TextView walkingMarker(TimelineSlot slot) {
        TextView marker = new TextView(this);
        String text = slot.walkingMinutesToNext().isPresent()
                ? "Walk " + slot.walkingMinutesToNext().getAsInt() + "m"
                : "Walk ?";
        marker.setText(text);
        marker.setContentDescription(text+" walking time to the next selected act");
        WackenTheme.textColor(marker, COLOR_AMBER);
        marker.setTextSize(14);
        marker.setTypeface(Typeface.DEFAULT_BOLD);
        marker.setGravity(Gravity.CENTER_VERTICAL);
        marker.setSingleLine(true);
        marker.setEllipsize(TextUtils.TruncateAt.END);
        marker.setPadding(calendarDp(4), 0, calendarDp(4), 0);
        marker.setBackground(WackenTheme.panelBackground(this, WackenTheme.PANEL, COLOR_AMBER, 4));
        return marker;
    }

    private FrameLayout.LayoutParams walkingMarkerLayout(
            ScheduleCalendarLayout layout,
            ScheduleDecisionCandidate from,
            ScheduleDecisionCandidate to
    ) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                calendarDp(72),
                calendarDp(22)
        );
        params.leftMargin = calendarDp(Math.max(0, (layout.walkingMarkerOffsetMinutes(from, to) * HOUR_WIDTH_DP / 60) - 36));
        params.topMargin = calendarDp(TIME_HEADER_HEIGHT_DP
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
        AppearanceController.bind(panel,"scheduleBlock",(view,palette)->view.setBackground(slotBackground(style,palette)));
        panel.setTag("schedule-slot:"+ScheduleManualSelections.conflictKey(slot));
        panel.setFocusable(true);panel.setMinimumWidth(dp(48));panel.setMinimumHeight(dp(48));
        panel.setAccessibilityDelegate(new View.AccessibilityDelegate() {
            @Override public void onInitializeAccessibilityNodeInfo(View host,android.view.accessibility.AccessibilityNodeInfo info) {
                super.onInitializeAccessibilityNodeInfo(host,info);
                info.setClassName(Button.class.getName());
            }
        });
        panel.setContentDescription("Open schedule decision for "+ScheduleBandDisplayName.clean(visible.bandName())+", "
                +visible.start().toLocalDate()+", "+content.timeRangeLine()+", "+visible.stageName()+", "
                +RatingValueText.describe(visible.rating())+", "+visible.status()+(style.scratched()?", skip lower-rated overlap":""));
        panel.setPadding(dp(10), dp(6), dp(10), dp(6));
        panel.setClickable(true);
        panel.setOnClickListener(view -> showDecisionDetails(slot));

        TextView timeRange = new TextView(this);
        timeRange.setText(content.timeRangeLine());
        WackenTheme.textColor(timeRange, COLOR_MUTED);
        timeRange.setTextSize(14);
        timeRange.setTypeface(Typeface.DEFAULT_BOLD);
        timeRange.setSingleLine(false);
        timeRange.setMaxLines(2);
        timeRange.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        panel.addView(timeRange);

        TextView band = new TextView(this);
        band.setText(content.bandLine());
        WackenTheme.textColor(band, COLOR_TEXT);
        band.setTextSize(16);
        band.setTypeface(Typeface.DEFAULT_BOLD);
        band.setSingleLine(true);
        band.setEllipsize(TextUtils.TruncateAt.END);
        band.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        panel.addView(band);

        content.lostAlternativeLine().ifPresent(lost -> {
            TextView alternative = new TextView(this);
            alternative.setText(lost);
            WackenTheme.textColor(alternative, COLOR_MUTED);
            alternative.setTextSize(14);
            alternative.setSingleLine(true);
            alternative.setEllipsize(TextUtils.TruncateAt.END);
            alternative.setPadding(0, dp(5), 0, 0);
            alternative.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            panel.setContentDescription(panel.getContentDescription()+", "+lost);
            panel.addView(alternative);
        });
        return panel;
    }

    private void showDecisionDetails(TimelineSlot slot) {
        ScrollView scroll = new ScrollView(this);
        decisionKey=ScheduleManualSelections.conflictKey(slot);
        scroll.setContentDescription("Schedule decision");
        scroll.setBackground(detailDialogBackground());

        LinearLayout detail = new LinearLayout(this);
        detail.setOrientation(LinearLayout.VERTICAL);
        detail.setPadding(dp(18), dp(16), dp(18), dp(10));
        detail.setBackground(detailDialogBackground());

        TextView title = detailText(ScheduleBandDisplayName.clean(manualSelections.visibleCandidate(slot).bandName()), COLOR_TEXT, 24, true);
        WackenTheme.heading(title);
        WackenTheme.textColor(title, COLOR_AMBER);
        title.setPadding(0, 0, 0, dp(14));
        detail.addView(title);

        detail.addView(detailSection("Chosen act"));
        final AlertDialog[] dialog = new AlertDialog[1];
        java.util.List<ScheduleDecisionCandidate> candidates = scheduleFilter()
                .visibleCandidates(manualSelections.detailCandidates(slot));
        if (candidates.isEmpty()) {
            detail.addView(detailText("No acts match the active filters.", COLOR_MUTED, 14, false));
        } else detail.addView(candidateView(slot, candidates.get(0), candidates, () -> dialog[0].dismiss()));

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
        decisionDialog=dialog[0];decisionScroll=scroll;
        dialog[0].setOnDismissListener(which->{
            if(decisionDialog==dialog[0]){decisionDialog=null;decisionScroll=null;decisionKey=null;decisionY=0;}
        });
        scroll.post(()->{if(decisionDialog==dialog[0])scroll.scrollTo(0,decisionY);});
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

        Button name=WackenTheme.actionButton(this,ScheduleBandDisplayName.clean(candidate.bandName())+" "+stars(candidate.rating()),
                WackenTheme.ButtonStyle.SECONDARY,view->{openBandDetail(candidate);afterSelect.run();});
        name.setContentDescription("Open band details for "+candidate.bandName());
        name.setTextSize(16);
        panel.addView(name);
        if (candidate.hasPersonRatings()) {
            TextView personRatings = detailText(
                    candidate.personRatingSummary(),
                    COLOR_MUTED,
                    14,
                    false
            );
            personRatings.setPadding(0, dp(2), 0, dp(2));
            panel.addView(personRatings);
        }

        TextView facts = detailText(
                candidate.stageName()+"\n"+candidate.start().toLocalDate().format(DATE)+"\n"
                        +candidate.start().format(TIME)+" - "+candidate.end().format(TIME),
                COLOR_MUTED,
                14,
                false
        );
        panel.addView(facts);

        TextView status = detailText(candidate.status(), candidate.selected() ? COLOR_ACCENT : COLOR_AMBER, 14, true);
        panel.addView(status);
        walkingContext(candidate, candidates).ifPresent(context ->
                panel.addView(detailText(context, COLOR_AMBER, 14, true))
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
                    decisionKey=null;decisionY=0;
                    rerender();
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
                    decisionKey=null;decisionY=0;
                    rerender();
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
        WackenTheme.textColor(view,color);
        view.setTextSize(size);
        view.setPadding(0, dp(2), 0, dp(2));
        if (bold) {
            view.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return view;
    }

    private TextView detailSection(String text) {
        TextView section = detailText(text, COLOR_ACCENT, 18, true);
        section.setPadding(0, dp(10), 0, dp(4));
        return section;
    }

    private GradientDrawable detailDialogBackground() {
        return WackenTheme.panelBackground(this,WackenTheme.PANEL,COLOR_GRID,6);
    }

    private Drawable slotBackground(ScheduleBlockStyle style,AppearancePreference.Palette palette) {
        StateListDrawable states=new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused},blockDrawable(style,palette,palette.text(),3));
        states.addState(new int[]{},blockDrawable(style,palette,style.borderColor(palette),2));
        return states;
    }

    private Drawable blockDrawable(ScheduleBlockStyle style,AppearancePreference.Palette palette,int border,int width) {
        return new ScheduleBlockDrawable(style.fillColor(palette),border,scratchColor(style.borderColor(palette)),
                style.scratched(),dp(6),dp(width),calendarDp(14),calendarDp(34));
    }

    private int scratchColor(int borderColor) {
        return Color.argb(
                192,
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
        WackenTheme.textColor(message,color);
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

    private int calendarDp(int value) {
        return dp(Math.round(value*Math.max(1f,getResources().getConfiguration().fontScale)));
    }

    private int stageLabelWidth() {
        return dp(Math.min(STAGE_LABEL_WIDTH_DP,Math.max(72,getResources().getConfiguration().screenWidthDp/3)));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private static final class VerticalDottedLineView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        VerticalDottedLineView(Context context, int color) {
            super(context);
            float density = context.getResources().getDisplayMetrics().density;
            AppearanceController.bind(this,"gridPaint",(view,palette)->{
                ((VerticalDottedLineView)view).paint.setColor(WackenTheme.resolve(color,palette));view.invalidate();
            });
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
