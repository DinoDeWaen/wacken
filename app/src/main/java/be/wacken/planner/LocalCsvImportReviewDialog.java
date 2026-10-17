package be.wacken.planner;

import android.app.*;
import android.view.*;
import android.widget.*;
import be.wacken.planner.application.*;
import java.util.*;
import java.util.function.*;

/** A staged, local-only review; retained state holds data and work, never an Activity. */
final class LocalCsvImportReviewDialog {
    static final class State {
        final FestivalCsvFiles files;
        final Supplier<ReviewFestivalCsvUseCase> factory;
        final Set<String> accepted=new LinkedHashSet<>();
        volatile ReviewFestivalCsvUseCase app;
        volatile ReviewFestivalCsvUseCase.Review review;
        volatile ImportFestivalCsvResult result;
        volatile List<String> messages=List.of("Reading selected files…");
        volatile boolean busy,applying,closed;
        volatile Runnable changed;
        State(FestivalCsvFiles files,Supplier<ReviewFestivalCsvUseCase> factory){this.files=files;this.factory=factory;}
        void notifyUi(){Runnable callback=changed;if(callback!=null)callback.run();}
        void load() {
            if(busy||closed)return;busy=true;result=null;messages=List.of("Reading selected files…");notifyUi();
            new Thread(()->{
                try {if(app==null)app=factory.get();review=app.review(files);messages=List.of("Choose each change to accept. Unchecked rows stay unchanged.");}
                catch(RuntimeException failure){messages=List.of(message(failure));}
                finally {busy=false;notifyUi();}
            },"review-local-csv").start();
        }
        void apply() {
            if(busy||closed||review==null)return;
            Set<String> choices=Set.copyOf(accepted);busy=true;applying=true;result=null;
            messages=List.of("Validating and saving accepted local changes…");notifyUi();
            new Thread(()->{
                try {result=app.apply(review,choices);messages=result.success()?List.of("Accepted local changes saved."):result.errors();}
                catch(RuntimeException failure){result=ImportFestivalCsvResult.failure(List.of(message(failure)));messages=result.errors();}
                finally {applying=false;busy=false;notifyUi();}
            },"apply-local-csv").start();
        }
        private static String message(RuntimeException error){return error.getMessage()==null?"Could not review or save this import. Existing data was kept.":error.getMessage();}
    }
    private final Activity activity;
    final State state;
    private final Consumer<ImportFestivalCsvResult> complete;
    private AlertDialog dialog;
    private ListView list;
    private TextView status;
    private final Runnable changed;
    private ReviewFestivalCsvUseCase.Review rendered;
    private boolean renderedBusy;
    LocalCsvImportReviewDialog(Activity activity,State state,Consumer<ImportFestivalCsvResult> complete) {
        this.activity=activity;this.state=state;this.complete=complete;
        this.changed=()->activity.runOnUiThread(()->{if(!activity.isFinishing()&&!activity.isDestroyed())render();});
    }
    void show() {
        LinearLayout panel=new LinearLayout(activity);panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(12),dp(8),dp(12),dp(8));panel.setBackgroundColor(WackenTheme.BACKGROUND);
        TextView scope=label("Local CSV review. Shared data may refresh this cache on the next sync. Ratings and archived data are kept.",WackenTheme.MUTED);
        panel.addView(scope);status=label("",WackenTheme.TEXT);panel.addView(status);
        list=new ListView(activity);panel.addView(list,new LinearLayout.LayoutParams(-1,dp(340)));
        dialog=new AlertDialog.Builder(activity).setTitle("Review local festival changes").setView(panel)
            .setPositiveButton("Apply accepted changes",null).setNeutralButton("Reload review",null)
            .setNegativeButton("Cancel",(d,w)->cancel()).setOnCancelListener(d->cancel()).create();
        dialog.setOnShowListener(d->{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->state.apply());
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v->{
                state.accepted.clear();state.review=null;state.load();
            });render();
        });
        state.changed=changed;dialog.show();
        if(state.review==null&&!state.busy)state.load();else render();
    }
    void detach(boolean finishing) {
        if(state.changed==changed)state.changed=null;
        if(finishing&&!state.applying)state.closed=true;
        if(dialog!=null)dialog.dismiss();
    }
    private void cancel() {
        if(state.applying)return;
        state.closed=true;state.changed=null;complete.accept(null);
    }
    private void render() {
        if(dialog==null||!dialog.isShowing())return;
        if(state.result!=null&&state.result.success()) {
            state.closed=true;state.changed=null;dialog.dismiss();complete.accept(state.result);return;
        }
        if(rendered!=state.review||renderedBusy!=state.busy) {
            int position=list.getFirstVisiblePosition();
            rendered=state.review;renderedBusy=state.busy;list.setAdapter(new Rows());list.setSelection(position);
        }
        status.setText((state.review==null?"":state.review.festivalName()+" · "+state.accepted.size()+" accepted\n")+String.join("\n",state.messages));
        dialog.setCancelable(!state.applying);
        Button apply=dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if(apply!=null)apply.setEnabled(!state.busy&&state.review!=null&&!state.accepted.isEmpty());
        Button reload=dialog.getButton(AlertDialog.BUTTON_NEUTRAL);if(reload!=null)reload.setEnabled(!state.busy);
        Button cancel=dialog.getButton(AlertDialog.BUTTON_NEGATIVE);if(cancel!=null)cancel.setEnabled(!state.applying);
        list.setEnabled(!state.busy);
    }
    private final class Rows extends BaseAdapter {
        public int getCount(){return state.review==null?0:state.review.changes().size();}
        public Object getItem(int position){return state.review.changes().get(position);}
        public long getItemId(int position){return position;}
        public View getView(int position,View reused,ViewGroup parent) {
            var row=(ReviewFestivalCsvUseCase.Change)getItem(position);
            LinearLayout view=new LinearLayout(activity);view.setOrientation(LinearLayout.VERTICAL);
            view.setPadding(dp(8),dp(6),dp(8),dp(10));view.setBackgroundColor(WackenTheme.PANEL);
            CheckBox accept=new CheckBox(activity);accept.setText("Accept "+row.operation()+" · "+row.kind()+" · "+row.identity());
            accept.setTextColor(WackenTheme.TEXT);accept.setContentDescription("Accept "+row.key());
            accept.setChecked(state.accepted.contains(row.key()));accept.setEnabled(row.valid()&&!state.busy);
            accept.setOnCheckedChangeListener((button,checked)->{
                if(state.busy||!row.valid())return;
                if(checked)state.accepted.add(row.key());else state.accepted.remove(row.key());render();
            });view.addView(accept);
            view.addView(label("Before: "+describe(row.before()),WackenTheme.MUTED));
            view.addView(label("After: "+describe(row.after()),WackenTheme.TEXT));
            view.addView(label(row.valid()?"Valid row. References and overlaps are checked with all accepted changes."
                :"Invalid: "+String.join("; ",row.errors()),row.valid()?WackenTheme.MUTED:WackenTheme.RED));
            return view;
        }
    }
    private static String describe(Map<String,String> fields) {
        if(fields.isEmpty())return "absent";
        List<String> values=new ArrayList<>();fields.forEach((name,value)->{if(!value.isBlank())values.add(name+"="+value);});
        return String.join("; ",values);
    }
    private TextView label(String text,int color){TextView view=new TextView(activity);view.setText(text);view.setTextColor(color);view.setPadding(0,dp(4),0,dp(4));return view;}
    private int dp(int value){return (int)(value*activity.getResources().getDisplayMetrics().density);}
}
