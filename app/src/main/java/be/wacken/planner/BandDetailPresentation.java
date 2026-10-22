package be.wacken.planner;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import be.wacken.planner.application.BandDetailItem;
import java.util.List;
import java.util.function.BiConsumer;

/** Shared read-only widgets; Activities retain editing, correction and navigation decisions. */
final class BandDetailPresentation {
    private final Activity activity;

    BandDetailPresentation(Activity activity) {
        this.activity = activity;
    }

    View render(BandDetailItem detail, BiConsumer<String, ImageView> loadImage, List<View> sections) {
        ScrollView scroll = new ScrollView(activity);
        scroll.setId(R.id.band_detail_scroll);
        WackenTheme.backgroundColor(scroll, WackenTheme.BACKGROUND);
        LinearLayout screen = new LinearLayout(activity);
        screen.setOrientation(LinearLayout.VERTICAL);
        screen.setGravity(Gravity.CENTER_HORIZONTAL);
        WackenTheme.backgroundColor(screen, WackenTheme.BACKGROUND);
        screen.setPadding(dp(16), dp(16), dp(16), dp(16));
        scroll.addView(screen, new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));

        TextView title = new TextView(activity);
        title.setText(detail.bandName());
        WackenTheme.textColor(title, WackenTheme.AMBER);
        title.setTextSize(26);
        WackenTheme.heading(title);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, dp(8), 0, dp(10));
        screen.addView(title);

        LinearLayout main = new LinearLayout(activity);
        main.setOrientation(narrowScreen() ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        main.setGravity(Gravity.CENTER);
        main.setPadding(dp(8), dp(8), dp(8), dp(8));
        screen.addView(main, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        detail.imageUrl().ifPresent(url -> main.addView(imagePanel(url, loadImage)));

        LinearLayout detailSections = new LinearLayout(activity);
        detailSections.setOrientation(LinearLayout.VERTICAL);
        detailSections.setGravity(Gravity.CENTER_HORIZONTAL);
        detailSections.setPadding(narrowScreen() ? 0 : dp(10), 0, 0, 0);
        detailSections.setLayoutParams(new LinearLayout.LayoutParams(narrowScreen() ? LinearLayout.LayoutParams.MATCH_PARENT : 0,
                LinearLayout.LayoutParams.WRAP_CONTENT, narrowScreen() ? 0 : 1));
        main.addView(detailSections);
        for (View section : sections) detailSections.addView(section);
        detail.biography().ifPresent(text -> screen.addView(paragraphPanel(text)));
        return scroll;
    }

    TextView context() {
        TextView context = new TextView(activity);
        WackenTheme.textColor(context, WackenTheme.TEXT);
        context.setContentDescription("Rating context");
        context.setTextSize(14);
        context.setPadding(dp(8),dp(8),dp(8),dp(8));
        return context;
    }

    LinearLayout section() {
        LinearLayout section = new LinearLayout(activity);
        section.setOrientation(LinearLayout.VERTICAL);
        section.setGravity(Gravity.CENTER_HORIZONTAL);
        section.setPadding(dp(12), dp(8), dp(12), dp(10));
        section.setBackground(WackenTheme.panelBackground(activity, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setMargins(0, 0, 0, dp(10));
        section.setLayoutParams(layout);
        return section;
    }

    TextView sectionTitle(String text, boolean spacious) {
        TextView title = new TextView(activity);
        title.setText(text);
        WackenTheme.textColor(title, WackenTheme.AMBER);
        title.setTextSize(18);
        WackenTheme.heading(title);
        title.setGravity(Gravity.START);
        title.setPadding(0, spacious ? dp(16) : 0, 0, dp(6));
        return title;
    }

    LinearLayout history(BandDetailItem detail) {
        LinearLayout section = section();
        section.addView(sectionTitle("Personal History", false));
        for (var item : detail.personalRatingHistory()) {
            TextView line = new TextView(activity);
            line.setText(item.displayText());
            WackenTheme.textColor(line, WackenTheme.TEXT);
            line.setTextSize(14);
            line.setGravity(Gravity.START);
            line.setPadding(0, dp(3), 0, dp(3));
            section.addView(line);
        }
        return section;
    }

    LinearLayout runningOrder(String stage, String day, String time) {
        LinearLayout section = section();
        section.addView(sectionTitle("Running Order", false));
        section.addView(infoLine("Stage", stage));
        section.addView(infoLine("Day", day));
        section.addView(infoLine("Time", time));
        return section;
    }

    LinearLayout links(BandDetailItem detail, Button home) {
        LinearLayout section = section();
        section.addView(sectionTitle("Band Links", false));
        LinearLayout links = new LinearLayout(activity);
        links.setOrientation(LinearLayout.VERTICAL);
        links.setGravity(Gravity.CENTER);
        links.addView(home);
        detail.youtubeUrl().ifPresent(url -> links.addView(musicAction("YouTube",url)));
        detail.spotifyUrl().ifPresent(url -> links.addView(musicAction("Spotify",url)));
        if(detail.youtubeUrl().isEmpty()&&detail.spotifyUrl().isEmpty())links.addView(infoLine("Music links","Not available"));
        section.addView(links);
        return section;
    }

    TextView infoLine(String label, String value) {
        TextView line = new TextView(activity);
        line.setText(label + ": " + value);
        WackenTheme.textColor(line, WackenTheme.TEXT);
        line.setTextSize(15);
        line.setGravity(Gravity.CENTER_HORIZONTAL);
        line.setPadding(0, dp(3), 0, dp(3));
        return line;
    }

    TextView groupRatings(BandDetailItem detail) {
        TextView ratings = new TextView(activity);
        ratings.setText(detail.hasPersonRatings() ? "Group: " + detail.personRatingSummary() : "Group ratings: not synced yet");
        WackenTheme.textColor(ratings, WackenTheme.TEXT);
        ratings.setTextSize(13);
        ratings.setGravity(Gravity.CENTER_HORIZONTAL);
        ratings.setPadding(0, dp(6), 0, dp(2));
        ratings.setContentDescription("Read-only group planning ratings. "+ratings.getText());
        return ratings;
    }

    private Button musicAction(String platform,String url) {
        Button button=WackenTheme.actionButton(activity,platform,WackenTheme.ButtonStyle.SECONDARY,v->ExternalLinks.open(activity,url));
        button.setContentDescription("Open "+platform);
        return button;
    }

    private ImageView imagePanel(String url, BiConsumer<String, ImageView> loadImage) {
        ImageView image = new ImageView(activity);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setContentDescription("Band image");
        image.setBackground(WackenTheme.panelBackground(activity, WackenTheme.PANEL, WackenTheme.GRID, 5));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(dp(140), dp(140));
        layout.setMargins(0, 0, narrowScreen() ? 0 : dp(10), narrowScreen() ? dp(12) : 0);
        image.setLayoutParams(layout);
        loadImage.accept(url, image);
        return image;
    }

    private View paragraphPanel(String text) {
        FrameLayout panel = new FrameLayout(activity);
        panel.setBackground(WackenTheme.panelBackground(activity, WackenTheme.PANEL, WackenTheme.GRID, 6));
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setMargins(0, dp(14), 0, 0);
        panel.setLayoutParams(layout);
        TextView paragraph = new TextView(activity);
        paragraph.setText(text);
        WackenTheme.textColor(paragraph, WackenTheme.TEXT);
        paragraph.setTextSize(15);
        paragraph.setGravity(Gravity.START);
        paragraph.setLineSpacing(dp(2), 1.0f);
        paragraph.setPadding(dp(14), dp(12), dp(14), dp(12));
        panel.addView(paragraph);
        return panel;
    }

    private int dp(int value) {
        return WackenTheme.dp(activity, value);
    }

    private boolean narrowScreen() {
        return activity.getResources().getConfiguration().fontScale>=1.5f
                ||BandDetailLayoutPolicy.stacksSections(activity.getResources().getConfiguration().screenWidthDp);
    }
}
