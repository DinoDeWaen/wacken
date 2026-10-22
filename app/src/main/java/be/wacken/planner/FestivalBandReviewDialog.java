package be.wacken.planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.*;
import java.util.*;
import java.util.function.Consumer;
import be.wacken.planner.application.ReviewFestivalBandsUseCase;
import be.wacken.planner.domain.*;

/** Staged choices only. Creation runs after every row and the final summary are confirmed. */
final class FestivalBandReviewDialog {
    private final Activity activity;
    private final List<ReviewFestivalBandsUseCase.Proposal> proposals;
    private final Consumer<List<ReviewedFestivalBand>> confirmed;
    private final Runnable cancelled;
    private final List<ReviewedFestivalBand> choices=new ArrayList<>();
    private AlertDialog dialog;
    private Spinner selection;
    private CheckBox remember;
    private int position;

    FestivalBandReviewDialog(Activity activity,List<ReviewFestivalBandsUseCase.Proposal> proposals,
                             Consumer<List<ReviewedFestivalBand>> confirmed,Runnable cancelled) {
        this.activity=activity;this.proposals=proposals;this.confirmed=confirmed;this.cancelled=cancelled;
    }
    void show() { next(); }
    void dismiss() { if(dialog!=null)dialog.dismiss(); }
    private void next() {
        // Exact canonical names retain their published reuse behavior.
        while(position<proposals.size() && proposals.get(position).exactMatch().isPresent()) {
            var row=proposals.get(position++);
            choices.add(new ReviewedFestivalBand(row.uploadedName(),row.exactMatch(),false));
        }
        if(position==proposals.size()) { summary(); return; }
        var row=proposals.get(position);
        LinearLayout form=new LinearLayout(activity);form.setOrientation(LinearLayout.VERTICAL);
        int padding=(int)(16*activity.getResources().getDisplayMetrics().density);form.setPadding(padding,padding,padding,padding);
        TextView explanation=new TextView(activity);
        explanation.setText("Choose a catalog band or create a separate band. Suggestions are not selected automatically.");
        explanation.setTextSize(16);WackenTheme.textColor(explanation,WackenTheme.TEXT);form.addView(explanation);
        List<String> labels=new ArrayList<>(List.of("Choose a match…","Create new band: "+row.uploadedName()));
        row.suggestions().forEach(b->labels.add(b.band().name()));
        selection=new Spinner(activity);
        selection.setContentDescription("Band identity for "+row.uploadedName());
        WackenTheme.reviewOptions(selection,labels);form.addView(selection);
        remember=new CheckBox(activity);remember.setText("Remember this spelling as an alias");remember.setEnabled(false);WackenTheme.reviewChoice(remember);form.addView(remember);
        selection.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            public void onItemSelected(android.widget.AdapterView<?> parent,android.view.View view,int index,long id) {
                remember.setEnabled(index>=2);if(index<2)remember.setChecked(false);
                if(dialog!=null && dialog.getButton(AlertDialog.BUTTON_POSITIVE)!=null)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(index>0);
            }
        });
        ScrollView reviewScroll=new ScrollView(activity);reviewScroll.addView(form);
        dialog=new AlertDialog.Builder(activity).setTitle("Review "+(position+1)+"/"+proposals.size()+": "+row.uploadedName())
            .setView(reviewScroll).setNegativeButton("Cancel",(d,w)->cancelled.run()).setPositiveButton("Confirm choice",null)
            .setOnCancelListener(d->cancelled.run()).create();
        dialog.setOnShowListener(d->{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{
                int index=selection.getSelectedItemPosition();if(index==0)return;
                choices.add(new ReviewedFestivalBand(row.uploadedName(),index==1?Optional.empty():Optional.of(row.suggestions().get(index-2)),remember.isChecked()));
                position++;dialog.dismiss();next();
            });
        });
        dialog.show();WackenTheme.dialog(dialog);
    }
    private void summary() {
        StringBuilder text=new StringBuilder("Create the festival using these reviewed choices?\n\n");
        for(var row:choices)text.append(row.uploadedName()).append(" → ")
            .append(row.selected().map(b->b.band().name()).orElse("New band")).append(row.rememberAlias()?" (remember alias)":"").append('\n');
        TextView summary=new TextView(activity);summary.setText(text);summary.setTextSize(16);WackenTheme.textColor(summary,WackenTheme.TEXT);summary.setPadding(24,16,24,16);
        ScrollView scroll=new ScrollView(activity);scroll.addView(summary);
        dialog=new AlertDialog.Builder(activity).setTitle("Confirm festival bands").setView(scroll)
            .setNegativeButton("Cancel",(d,w)->cancelled.run()).setOnCancelListener(d->cancelled.run())
            .setPositiveButton("Create festival",(d,w)->confirmed.accept(List.copyOf(choices))).show();
        WackenTheme.dialog(dialog);
    }
}
