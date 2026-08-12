package be.wacken.planner;

import java.util.stream.Collectors;

import be.wacken.planner.application.FestivalStartState;
import be.wacken.planner.domain.Festival;

final class FestivalStartScreenContent {
    private final String title;
    private final String subtitle;
    private final String statusText;
    private final boolean showBandList;
    private final boolean showArchiveAction;
    private final boolean showAddFestivalAction;

    private FestivalStartScreenContent(
            String title,
            String subtitle,
            String statusText,
            boolean showBandList,
            boolean showArchiveAction,
            boolean showAddFestivalAction
    ) {
        this.title = title;
        this.subtitle = subtitle;
        this.statusText = statusText;
        this.showBandList = showBandList;
        this.showArchiveAction = showArchiveAction;
        this.showAddFestivalAction = showAddFestivalAction;
    }

    static FestivalStartScreenContent from(FestivalStartState state, String userEmail) {
        if (state.hasActiveFestival()) {
            Festival activeFestival = state.activeFestival().orElseThrow();
            return new FestivalStartScreenContent(
                    activeFestival.name(),
                    "Line-up ratings for " + userEmail,
                    "",
                    true,
                    true,
                    false
            );
        }
        String archivedFestivals = state.archivedFestivals()
                .stream()
                .map(Festival::name)
                .collect(Collectors.joining(", "));
        String status = archivedFestivals.isBlank()
                ? "No archived festivals yet. Add the next festival to start planning."
                : "Archived festivals (read-only): " + archivedFestivals;
        return new FestivalStartScreenContent(
                "Festival archive",
                "No active festival",
                status,
                false,
                false,
                true
        );
    }

    String title() {
        return title;
    }

    String subtitle() {
        return subtitle;
    }

    String statusText() {
        return statusText;
    }

    boolean showBandList() {
        return showBandList;
    }

    boolean showArchiveAction() {
        return showArchiveAction;
    }

    boolean showAddFestivalAction() {
        return showAddFestivalAction;
    }
}
