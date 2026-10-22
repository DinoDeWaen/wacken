package be.wacken.planner;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Native value choices shared by active editors and read-only archive presentation. */
final class RatingStarsView extends LinearLayout {
    private static final int MAX_RATING=5;
    private final TextView[] stars=new TextView[MAX_RATING];
    private int savedRating;
    private boolean explicitRating;
    private String scope="Planning rating";
    private OnRatingSelectedListener onRatingSelected;

    RatingStarsView(Context context,int savedRating,boolean explicitRating,int accentColor) {
        super(context);
        this.savedRating=clampRating(savedRating);
        this.explicitRating=explicitRating;
        setGravity(Gravity.CENTER_VERTICAL);
        for(int index=0;index<stars.length;index++) {
            final int value=index+1;
            Button star=WackenTheme.actionButton(context,"☆",WackenTheme.ButtonStyle.SECONDARY,
                    view->{if(editable())onRatingSelected.onSelected(value);});
            star.setTextSize(24);
            star.setPadding(0,WackenTheme.dp(context,8),0,WackenTheme.dp(context,8));
            WackenTheme.textColor(star,accentColor);
            stars[index]=star;
            addView(star);
        }
        setOnTouchListener((view,event)->{
            if(!editable())return false;
            int value=ratingAt(event.getX(),event.getY());
            if(event.getAction()==MotionEvent.ACTION_DOWN||event.getAction()==MotionEvent.ACTION_MOVE)renderStars(value,true);
            if(event.getAction()==MotionEvent.ACTION_UP) {
                onRatingSelected.onSelected(value);
                super.performClick();
            }
            if(event.getAction()==MotionEvent.ACTION_CANCEL)restoreRestingState();
            return true;
        });
        updateInteraction();
    }

    void setScope(String scope) {this.scope=scope;restoreRestingState();}

    void setOnRatingSelected(OnRatingSelectedListener listener) {
        onRatingSelected=listener;
        updateInteraction();
    }

    @Override public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(stars!=null&&stars[0]!=null)updateInteraction();
    }

    private boolean editable() {return isEnabled()&&onRatingSelected!=null;}

    private void updateInteraction() {
        boolean editable=editable();
        boolean stacked=editable&&(getResources().getConfiguration().fontScale>=1.5f
                ||getResources().getConfiguration().screenWidthDp<320);
        setOrientation(stacked?VERTICAL:HORIZONTAL);
        setClickable(editable);
        setFocusable(false);
        setImportantForAccessibility(editable?View.IMPORTANT_FOR_ACCESSIBILITY_NO:View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        for(TextView star:stars) {
            star.setEnabled(editable);star.setClickable(editable);star.setFocusable(editable);
            star.setImportantForAccessibility(editable?View.IMPORTANT_FOR_ACCESSIBILITY_YES:View.IMPORTANT_FOR_ACCESSIBILITY_NO);
            int target=editable?WackenTheme.dp(getContext(),48):0;
            star.setMinWidth(target);star.setMinimumWidth(target);
            star.setMinHeight(target);star.setMinimumHeight(target);
            LayoutParams layout=stacked?new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT)
                    :new LayoutParams(0,editable?LayoutParams.WRAP_CONTENT:LayoutParams.MATCH_PARENT,1);
            star.setLayoutParams(layout);
        }
        restoreRestingState();
    }

    void bind(int rating,boolean explicit) {
        savedRating=clampRating(rating);explicitRating=explicit;restoreRestingState();
    }

    void applySavedRating(int rating) {
        boolean changed=savedRating!=rating;
        savedRating=clampRating(rating);explicitRating=savedRating>0;
        restoreRestingState();
        if(changed&&editable())announceForAccessibility(scope+", "+RatingValueText.describe(savedRating)
                +", saved on this device, pending sync");
    }

    void showAvailableRating() {renderStars(savedRating,true);}
    void restoreRestingState() {renderStars(savedRating,explicitRating||editable());}

    private int ratingAt(float x,float y) {
        float fraction=getOrientation()==VERTICAL?y/Math.max(getHeight(),1):x/Math.max(getWidth(),1);
        return Math.max(1,Math.min(MAX_RATING,(int)Math.ceil(fraction*MAX_RATING)));
    }

    private int clampRating(int rating) {return Math.max(0,Math.min(rating,MAX_RATING));}

    private void renderStars(int preview,boolean visible) {
        setContentDescription(scope+", "+RatingValueText.describe(savedRating)+(editable()?"":", read-only"));
        for(int index=0;index<stars.length;index++) {
            TextView star=stars[index];
            star.setText(index<preview?"★":"☆");
            boolean selected=index+1==savedRating;
            star.setSelected(selected);
            star.setContentDescription(scope+", "+RatingValueText.describe(index+1)+(selected?", selected":""));
            WackenTheme.ratingChoice(star);
            star.setVisibility(visible?VISIBLE:INVISIBLE);
        }
    }

    interface OnRatingSelectedListener {void onSelected(int rating);}
}
