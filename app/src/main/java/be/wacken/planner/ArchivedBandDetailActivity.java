package be.wacken.planner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import be.wacken.planner.application.BandDetailItem;
import be.wacken.planner.application.BiographyText;
import be.wacken.planner.application.PersonRatingStars;
import be.wacken.planner.application.PersonalRatingHistoryItem;
import be.wacken.planner.application.ViewArchivedFestivalHistoryUseCase;
import be.wacken.planner.domain.Band;

public final class ArchivedBandDetailActivity extends Activity {
    private final BandDetailPresentation presentation = new BandDetailPresentation(this);
    private final BandImageLoader<Bitmap> imageLoader =
            new BandImageLoader<>(BitmapFactory::decodeStream, this::runOnUiThread);
    public static final String EXTRA_FESTIVAL_ID = "be.wacken.planner.ARCHIVED_FESTIVAL_ID";
    public static final String EXTRA_BAND_NAME = "be.wacken.planner.ARCHIVED_BAND_NAME";
    public static final String EXTRA_STAGE = "be.wacken.planner.ARCHIVED_STAGE";
    public static final String EXTRA_DATE = "be.wacken.planner.ARCHIVED_DATE";
    public static final String EXTRA_TIME = "be.wacken.planner.ARCHIVED_TIME";
    public static final String EXTRA_YOUTUBE_URL = "be.wacken.planner.ARCHIVED_YOUTUBE_URL";
    public static final String EXTRA_SPOTIFY_URL = "be.wacken.planner.ARCHIVED_SPOTIFY_URL";

    private static final String TBA = "TBA";
    private AppRepositories repositories;
    private AuthSession session;
    private String festivalId;
    private String bandName;
    private java.util.function.Supplier<be.wacken.planner.application.ArchivedRatingCorrectionUseCase> correctionUseCase;
    private ArchivedRatingCorrectionDialog correctionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new AuthSessionStore(this).load();
        if (!session.isPresent()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        festivalId = getIntent().getStringExtra(EXTRA_FESTIVAL_ID);
        bandName = getIntent().getStringExtra(EXTRA_BAND_NAME);
        repositories = new AppRepositories(this);
        correctionUseCase=repositories::archivedRatingCorrections;
        refresh();
    }

    private void refresh() {
        ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history = new ViewArchivedFestivalHistoryUseCase(
                repositories.festivals(),
                repositories.festivalLineups(),
                repositories.festivalPlanningRatings(),
                repositories.personalBandRatings(),
                new be.wacken.planner.application.FestivalAfterRatingView(repositories.afterRatings(), festivalId)
        ).show(session.userId(), festivalId);
        Optional<Band> band = repositories.bands().findByName(bandName);
        setContentView(render(archivedDetail(session.userId(), bandName, band, history)));
    }

    private BandDetailItem archivedDetail(
            String userName,
            String bandName,
            Optional<Band> band,
            ViewArchivedFestivalHistoryUseCase.ArchivedFestivalHistory history
    ) {
        Optional<Integer> planningRating = history.planningRatings()
                .stream()
                .filter(item -> item.bandName().equals(bandName))
                .filter(item -> item.userName().equals(userName))
                .map(ViewArchivedFestivalHistoryUseCase.ArchivedPlanningRatingItem::rating)
                .findFirst();
        List<PersonRatingStars> groupPlanningRatings = history.planningRatings()
                .stream()
                .filter(item -> item.bandName().equals(bandName))
                .filter(item -> item.rating() > 0)
                .map(item -> new PersonRatingStars(item.userName(), item.rating()))
                .collect(Collectors.toList());
        List<PersonalRatingHistoryItem> bandHistory = history.personalRatings()
                .stream()
                .filter(item -> item.bandName().equals(bandName))
                .collect(Collectors.toList());
        Optional<be.wacken.planner.domain.Rating> festivalRealRating = repositories.afterRatings()
                .findByUserFestivalAndBand(userName,history.festivalId(),new Band(bandName))
                .map(be.wacken.planner.domain.FestivalAfterRating::rating);
        return new BandDetailItem(
                bandName,
                band.flatMap(value -> BiographyText.readable(value.biography())),
                band.flatMap(Band::imageUrl),
                planningRating.orElse(0),
                planningRating.isEmpty() || planningRating.get() == 0,
                band.flatMap(Band::youtubeUrl),
                band.flatMap(Band::spotifyUrl),
                festivalRealRating.map(be.wacken.planner.domain.Rating::value).orElse(0),
                festivalRealRating.isEmpty() || festivalRealRating.get().value() == 0,
                groupPlanningRatings,
                bandHistory
        );
    }

    private View render(BandDetailItem detail) {
        TextView context = presentation.context();
        repositories.ratingContext().show(session.userId(), festivalId, bandName)
                .ifPresent(value -> context.setText(String.join("\n", value.lines())));
        java.util.List<View> sections = new java.util.ArrayList<>();
        sections.add(context);
        sections.add(ratingSection(detail));
        sections.add(realRatingSection(detail));
        sections.add(correctionSection());
        if (detail.hasPersonalRatingHistory()) sections.add(personalRatingHistorySection(detail));
        sections.add(runningOrderSection());
        sections.add(linksSection(detail));
        return presentation.render(detail, this::loadImage, sections);
    }

    private LinearLayout correctionSection() {
        LinearLayout section=detailSection();
        section.addView(sectionTitle("Correction history"));
        var audit=repositories.ratingCorrectionHistory().findByUserFestivalAndBand(session.userId(),festivalId,new Band(bandName));
        for(var correction:audit) {
            section.addView(infoLine(correction.kind().name(),correction.previousValue()+" → "+correction.newValue()));
            section.addView(infoLine("Changed by",correction.userName()));
            section.addView(infoLine("Correction time",correction.correctedAt().toString()));
            section.addView(infoLine("Original recorded date",correction.originalRecordedAt().map(Object::toString).orElse("unknown")));
        }
        if(audit.isEmpty())section.addView(infoLine("History","No cached corrections"));
        boolean archived=repositories.festivals().findAll().stream().anyMatch(f->f.id().equals(festivalId)&&f.isArchived());
        Button edit=WackenTheme.actionButton(this,"Correct my archived rating",WackenTheme.ButtonStyle.SECONDARY,v->{
            correctionDialog=new ArchivedRatingCorrectionDialog(this,correctionUseCase.get(),session.userId(),festivalId,new Band(bandName),kind->
                kind==be.wacken.planner.domain.ArchivedRatingKind.AFTER
                    ?repositories.afterRatings().findByUserFestivalAndBand(session.userId(),festivalId,new Band(bandName)).map(r->r.rating().value()).orElse(0)
                    :repositories.festivalPlanningRatings().findByUserFestivalAndBand(session.userId(),festivalId,new Band(bandName)).map(be.wacken.planner.domain.Rating::value).orElse(0),
                this::refresh);
            correctionDialog.show();
        });
        edit.setEnabled(archived);section.addView(edit);return section;
    }

    @Override protected void onDestroy(){
        imageLoader.close();
        if(correctionDialog!=null)correctionDialog.dismiss();
        super.onDestroy();
    }

    private LinearLayout ratingSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Festival planning rating"));
        section.addView(disabledStars(detail.rating(), !detail.defaultRating(), WackenTheme.RED));
        section.addView(disabledResetButton("Clear archived planning rating"));
        section.addView(groupRatingsView(detail));
        return section;
    }

    private LinearLayout realRatingSection(BandDetailItem detail) {
        LinearLayout section = detailSection();
        section.addView(sectionTitle("Festival after-rating"));
        section.addView(disabledStars(detail.realRating(), !detail.defaultRealRating(), WackenTheme.AMBER));
        section.addView(disabledResetButton("Reset archived real rating"));
        return section;
    }

    private RatingStarsView disabledStars(int value, boolean explicitRating, int color) {
        RatingStarsView stars = new RatingStarsView(this, value, explicitRating, color);
        stars.setEnabled(false);
        stars.setClickable(false);
        stars.setFocusable(false);
        stars.setPadding(dp(12), 0, dp(12), 0);
        stars.showAvailableRating();
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(150), dp(42));
        layout.gravity = Gravity.CENTER_HORIZONTAL;
        stars.setLayoutParams(layout);
        return stars;
    }

    private Button disabledResetButton(String description) {
        Button reset = WackenTheme.actionButton(this, "Reset", WackenTheme.ButtonStyle.SECONDARY, null);
        reset.setEnabled(false);
        reset.setContentDescription(description);
        reset.setTextSize(13);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(96), dp(38));
        layout.gravity = Gravity.CENTER_HORIZONTAL;
        layout.setMargins(0, dp(2), 0, dp(4));
        reset.setLayoutParams(layout);
        return reset;
    }

    private LinearLayout personalRatingHistorySection(BandDetailItem detail) {
        return presentation.history(detail);
    }

    private LinearLayout runningOrderSection() {
        return presentation.runningOrder(valueExtra(EXTRA_STAGE), valueExtra(EXTRA_DATE), valueExtra(EXTRA_TIME));
    }

    private LinearLayout linksSection(BandDetailItem detail) {
        return presentation.links(detail, homeButton());
    }


    private void loadImage(String url, ImageView image) {
        imageLoader.load(url, bitmap -> {
            if (!isFinishing() && !isDestroyed()) image.setImageBitmap(bitmap);
        }, () -> {
            if (!isFinishing() && !isDestroyed()) image.setVisibility(View.GONE);
        });
    }

    private LinearLayout detailSection() {
        return presentation.section();
    }

    private TextView sectionTitle(String text) {
        return presentation.sectionTitle(text, false);
    }


    private TextView infoLine(String label, String value) {
        return presentation.infoLine(label, value);
    }

    private TextView groupRatingsView(BandDetailItem detail) {
        return presentation.groupRatings(detail);
    }

    private Button homeButton() {
        Button button = WackenTheme.iconButton(this, "⌂", "Back", WackenTheme.WHITE, 44, view -> finish());
        return button;
    }


    private String valueExtra(String key) {
        String value = getIntent().getStringExtra(key);
        if (value == null || value.isBlank()) {
            return TBA;
        }
        return value;
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density);
    }

    private boolean narrowScreen() {
        return BandDetailLayoutPolicy.stacksSections(getResources().getConfiguration().screenWidthDp);
    }
}
