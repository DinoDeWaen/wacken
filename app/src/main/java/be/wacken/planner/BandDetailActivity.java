package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Optional;

import be.wacken.planner.application.BandDetailItem;
import be.wacken.planner.application.MusicLinks;
import be.wacken.planner.application.RateBandResult;
import be.wacken.planner.application.RateBandUseCase;
import be.wacken.planner.application.ShowBandDetailUseCase;
import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.BandRepository;
import be.wacken.planner.domain.RatingRepository;

public final class BandDetailActivity extends Activity {
    public static final String EXTRA_BAND_NAME = "be.wacken.planner.BAND_NAME";
    public static final String EXTRA_RATING = "be.wacken.planner.RATING";
    public static final String EXTRA_DEFAULT_RATING = "be.wacken.planner.DEFAULT_RATING";
    public static final String EXTRA_YOUTUBE_URL = "be.wacken.planner.YOUTUBE_URL";
    public static final String EXTRA_SPOTIFY_URL = "be.wacken.planner.SPOTIFY_URL";

    private static final String CURRENT_USER = "my group";

    private BandRepository bands;
    private RatingRepository ratings;
    private Band selectedBand;
    private TextView validationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppRepositories repositories = new AppRepositories(this);
        bands = repositories.bands();
        ratings = repositories.ratings();

        selectedBand = new Band(getIntent().getStringExtra(EXTRA_BAND_NAME));
        if (bands.findByName(selectedBand.name()).isEmpty()) {
            bands.save(selectedBand);
        }
        seedExplicitRating();

        ShowBandDetailUseCase showBand = new ShowBandDetailUseCase(bands, ratings);
        BandDetailItem detail = showBand.showBand(CURRENT_USER, selectedBand.name(), musicLinksFromIntent())
                .orElseThrow();
        setContentView(render(detail));
    }

    private View render(BandDetailItem detail) {
        LinearLayout screen = new LinearLayout(this);
        screen.setOrientation(LinearLayout.VERTICAL);
        int padding = 32;
        screen.setPadding(padding, padding, padding, padding);

        TextView title = new TextView(this);
        title.setText(detail.bandName());
        title.setTextSize(28);
        screen.addView(title);

        TextView ratingLabel = new TextView(this);
        ratingLabel.setText(detail.defaultRating() ? "Selected rating: 1 (default)" : "Selected rating: " + detail.rating());
        screen.addView(ratingLabel);

        validationMessage = new TextView(this);
        screen.addView(validationMessage);

        RadioGroup ratingButtons = new RadioGroup(this);
        ratingButtons.setOrientation(RadioGroup.HORIZONTAL);
        for (int rating = 0; rating <= 4; rating++) {
            RadioButton button = new RadioButton(this);
            button.setId(View.generateViewId());
            button.setText(starsFor(rating));
            button.setTag(rating);
            ratingButtons.addView(button);
            if (rating == detail.rating()) {
                ratingButtons.check(button.getId());
            }
        }
        ratingButtons.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selected = group.findViewById(checkedId);
            int rating = (int) selected.getTag();
            RateBandResult result = new RateBandUseCase(ratings).rateBand(CURRENT_USER, selectedBand, rating);
            if (result.success()) {
                validationMessage.setText("Selected rating: " + rating);
            } else {
                validationMessage.setText(result.validationMessage().orElse(""));
            }
        });
        screen.addView(ratingButtons);

        detail.youtubeUrl().ifPresent(url -> screen.addView(linkButton("YouTube", url)));
        detail.spotifyUrl().ifPresent(url -> screen.addView(linkButton("Spotify", url)));

        return screen;
    }

    private Button linkButton(String label, String url) {
        Button button = new Button(this);
        button.setText(label);
        button.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
        return button;
    }

    private String starsFor(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int selected = 0; selected < rating; selected++) {
            stars.append("★");
        }
        for (int unselected = rating; unselected < 4; unselected++) {
            stars.append("☆");
        }
        return stars.toString();
    }

    private void seedExplicitRating() {
        boolean defaultRating = getIntent().getBooleanExtra(EXTRA_DEFAULT_RATING, true);
        if (!defaultRating && getIntent().hasExtra(EXTRA_RATING)) {
            ratings.save(CURRENT_USER, selectedBand, Rating.of(getIntent().getIntExtra(EXTRA_RATING, 1)));
        }
    }

    private MusicLinks musicLinksFromIntent() {
        return new MusicLinks(
                Optional.ofNullable(getIntent().getStringExtra(EXTRA_YOUTUBE_URL)),
                Optional.ofNullable(getIntent().getStringExtra(EXTRA_SPOTIFY_URL))
        );
    }
}
