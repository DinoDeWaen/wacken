package be.wacken.planner;

import android.app.*;
import android.text.InputType;
import android.view.View;
import android.widget.*;
import be.wacken.planner.application.ArchivedRatingCorrectionUseCase;
import be.wacken.planner.domain.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.ToIntFunction;

/** Explicit staged own-rating correction; remote saves always require confirmation. */
final class ArchivedRatingCorrectionDialog {
    private final Activity activity;
    private final ArchivedRatingCorrectionUseCase corrections;
    private final String user,festival;
    private final Band band;
    private final ToIntFunction<ArchivedRatingKind> localValue;
    private final Runnable onSaved;
    private ArchivedRatingReview reviewed;
    private ArchivedRatingKind kind=ArchivedRatingKind.AFTER;
    private long generation;
    private boolean saving;
    private AlertDialog dialog;
    private AlertDialog confirmation;
    private EditText proposed;
    private TextView status;
    private Spinner type;
    private Button reload;
    ArchivedRatingCorrectionDialog(Activity activity,ArchivedRatingCorrectionUseCase corrections,String user,
            String festival,Band band,ToIntFunction<ArchivedRatingKind> localValue,Runnable onSaved){
        this.activity=activity;this.corrections=corrections;this.user=user;this.festival=festival;
        this.band=band;this.localValue=localValue;this.onSaved=onSaved;
    }
    void show(){
        LinearLayout panel=new LinearLayout(activity);panel.setOrientation(LinearLayout.VERTICAL);panel.setPadding(24,16,24,16);
        type=new Spinner(activity);
        type.setAdapter(new ArrayAdapter<>(activity,android.R.layout.simple_spinner_dropdown_item,new String[]{"After-rating","Planning rating"}));
        type.setContentDescription("Archived rating kind");panel.addView(type);
        proposed=new EditText(activity);proposed.setInputType(InputType.TYPE_CLASS_NUMBER);
        proposed.setHint("Corrected rating");proposed.setContentDescription("Corrected archived rating");panel.addView(proposed);
        status=new TextView(activity);status.setText("Load the current rating before confirming a correction.");panel.addView(status);
        reload=new Button(activity);reload.setText("Reload current rating");reload.setOnClickListener(v->load());panel.addView(reload);
        dialog=new AlertDialog.Builder(activity).setTitle("Correct my archived rating").setView(panel)
                .setNegativeButton("Cancel",null).setPositiveButton("Save correction",null).create();
        dialog.setOnShowListener(v->{
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(button->confirm());
        });
        type.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){
            public void onNothingSelected(android.widget.AdapterView<?> parent){}
            public void onItemSelected(android.widget.AdapterView<?> parent,View view,int position,long id){
                if(saving)return;
                ArchivedRatingKind selected=position==0?ArchivedRatingKind.AFTER:ArchivedRatingKind.PLANNING;
                if(selected==kind&&generation>0)return;
                kind=selected;
                proposed.setText(String.valueOf(localValue.applyAsInt(kind)));load();
            }
        });
        dialog.show();
        proposed.setText(String.valueOf(localValue.applyAsInt(kind)));load();
    }
    private void load(){
        if(saving||!dialog.isShowing())return;
        long request=++generation;ArchivedRatingKind selected=kind;reviewed=null;
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        status.setText("Loading your current archived rating. You can stage a value while offline; reconnect to save.");
        new Thread(()->{
            try{
                var result=corrections.review(user,festival,band,selected);
                activity.runOnUiThread(()->{
                    if(!visible(request))return;
                    reviewed=result;status.setText("Current "+selected.name().toLowerCase(java.util.Locale.ROOT)+" rating: "+result.value()
                        +"\nOriginal recorded date: "+result.recordedAt().map(Object::toString).orElse("unknown")
                        +"\nReview your new value before saving. A band rating changes only while this festival is its source.");
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                });
            }catch(RuntimeException error){activity.runOnUiThread(()->{if(visible(request))status.setText("Could not load review. Reconnect and reload. "+message(error));});}
        },"review-archived-rating").start();
    }
    private void confirm(){
        if(reviewed==null||saving)return;
        int value;
        try{
            value=Integer.parseInt(proposed.getText().toString().trim());
            Rating.of(value);
            if(kind==ArchivedRatingKind.AFTER&&value==0)throw new IllegalArgumentException("Choose1–5 for an after-rating.");
        }catch(IllegalArgumentException error){status.setText("Choose "+(kind==ArchivedRatingKind.AFTER?"1–5":"0–5")+" before saving.");return;}
        if(value==reviewed.value()){status.setText("This is already the current rating. Choose another value or cancel.");return;}
        ArchivedRatingReview expected=reviewed;
        confirmation=new AlertDialog.Builder(activity).setTitle("Confirm archived correction")
                .setMessage("Change your "+kind.name().toLowerCase(java.util.Locale.ROOT)+" rating from "+expected.value()+" to "+value+"? The original recorded date and change history are retained.")
                .setNegativeButton("Cancel",null).setPositiveButton("Confirm correction",(d,w)->save(expected,value)).show();
    }
    void dismiss(){
        if(confirmation!=null)confirmation.dismiss();
        if(dialog!=null)dialog.dismiss();
    }
    private void save(ArchivedRatingReview expected,int value){
        saving=true;long request=++generation;
        dialog.setCancelable(false);dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);proposed.setEnabled(false);
        type.setEnabled(false);reload.setEnabled(false);
        status.setText("Saving correction and change history...");
        // Stable payload identity lets an uncertain save be retried without a second audit.
        String identity=new org.json.JSONArray().put(user).put(festival).put(band.name())
                .put(expected.kind().name()).put(expected.value()).put(expected.revision()).put(value).toString();
        String operation=UUID.nameUUIDFromBytes(identity.getBytes(StandardCharsets.UTF_8)).toString();
        new Thread(()->{
            try{
                corrections.correct(user,operation,expected,value);
                activity.runOnUiThread(()->{if(visible(request)){dialog.dismiss();onSaved.run();}});
            }catch(RuntimeException error){activity.runOnUiThread(()->{
                if(!visible(request))return;
                saving=false;dialog.setCancelable(true);dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(true);
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);proposed.setEnabled(true);
                type.setEnabled(true);reload.setEnabled(true);
                status.setText(message(error)+"\nRetry without changing the value to confirm the same operation, or reload a stale review.");
            });}
        },"correct-archived-rating").start();
    }
    private boolean visible(long request){return request==generation&&!activity.isFinishing()&&!activity.isDestroyed()&&dialog.isShowing();}
    private String message(RuntimeException error){return error.getMessage()==null?"Correction could not be confirmed.":error.getMessage();}
}
