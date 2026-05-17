package be.wacken.planner;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

final class RatingStarsView extends LinearLayout {
    private static final int MAX_RATING = 4;
    private static final int STAR_POSITIONS = 5;

    private final TextView[] stars = new TextView[STAR_POSITIONS];
    private final int accentColor;
    private int savedRating;
    private boolean explicitRating;
    private OnRatingSelectedListener onRatingSelected;

    RatingStarsView(Context context, int savedRating, boolean explicitRating, int accentColor) {
        super(context);
        this.savedRating = savedRating;
        this.explicitRating = explicitRating;
        this.accentColor = accentColor;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setClickable(true);
        setFocusable(true);

        for (int index = 0; index < STAR_POSITIONS; index++) {
            TextView star = new TextView(context);
            star.setTextSize(24);
            star.setTypeface(Typeface.DEFAULT_BOLD);
            star.setGravity(Gravity.CENTER);
            stars[index] = star;
            addView(star, new LayoutParams(0, LayoutParams.MATCH_PARENT, 1));
        }

        setOnHoverListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_HOVER_ENTER || event.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
                previewRating(ratingFromX(event.getX()));
            }
            if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                restoreRestingState();
            }
            return true;
        });
        setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                previewRating(ratingFromX(event.getX()));
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                int rating = ratingFromX(event.getX());
                if (onRatingSelected != null) {
                    onRatingSelected.onSelected(rating);
                }
                super.performClick();
            }
            return true;
        });

        restoreRestingState();
    }

    void setOnRatingSelected(OnRatingSelectedListener onRatingSelected) {
        this.onRatingSelected = onRatingSelected;
    }

    void bind(int savedRating, boolean explicitRating) {
        this.savedRating = Math.min(savedRating, MAX_RATING);
        this.explicitRating = explicitRating;
        restoreRestingState();
    }

    void applySavedRating(int rating) {
        savedRating = Math.min(rating, MAX_RATING);
        explicitRating = true;
        restoreRestingState();
    }

    void showAvailableRating() {
        renderStars(explicitRating ? savedRating : 0, true);
    }

    void restoreRestingState() {
        if (explicitRating) {
            renderStars(savedRating, true);
        } else {
            renderStars(0, false);
        }
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private void previewRating(int rating) {
        renderStars(rating, true);
    }

    private int ratingFromX(float x) {
        int width = Math.max(getWidth(), 1);
        int position = Math.max(1, Math.min(STAR_POSITIONS, (int) Math.ceil((x / width) * STAR_POSITIONS)));
        return Math.min(position, MAX_RATING);
    }

    private void renderStars(int rating, boolean visible) {
        for (int index = 0; index < stars.length; index++) {
            stars[index].setText(index < rating ? "★" : "☆");
            stars[index].setTextColor(accentColor);
            stars[index].setVisibility(visible ? VISIBLE : INVISIBLE);
        }
    }

    interface OnRatingSelectedListener {
        void onSelected(int rating);
    }
}
