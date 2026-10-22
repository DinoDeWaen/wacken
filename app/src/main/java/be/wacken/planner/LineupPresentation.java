package be.wacken.planner;

import be.wacken.planner.application.BandListItem;
import java.util.List;
import java.util.Locale;

/** Local display filtering of the already scoped and alphabetically ordered lineup. */
final class LineupPresentation {
    private LineupPresentation() { }

    static List<BandListItem> select(List<BandListItem> bands,String query,boolean unratedOnly) {
        String name=query.toLowerCase(Locale.ROOT);
        return bands.stream().filter(b->b.bandName().toLowerCase(Locale.ROOT).contains(name))
                .filter(b->!unratedOnly||b.rating()==0).toList();
    }

    static String planningLabel(int rating) {
        return "Planning: "+RatingValueText.describe(rating);
    }
}
