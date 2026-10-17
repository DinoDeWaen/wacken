package be.wacken.planner.application;

import be.wacken.planner.domain.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

final class FestivalDetailRatingScopeTest {
    @Test void openedFestivalDoesNotFollowActiveFestivalOrWriteAfterArchive() {
        var festivals=mock(FestivalRepository.class);
        var lineup=mock(FestivalLineupRepository.class);
        var planning=mock(FestivalPlanningRatingRepository.class);
        var band=new Band("Known");
        when(festivals.findAll()).thenReturn(List.of(Festival.active("opened","Opened")));
        when(lineup.findByFestival("opened")).thenReturn(List.of(new FestivalLineupEntry("opened",band,"Known")));
        var view=new FestivalPlanningRatingView(festivals,lineup,planning,"group","opened");
        view.save("member",band,Rating.of(5));
        verify(planning).save("group","member","opened",band,Rating.of(5));
        when(festivals.findAll()).thenReturn(List.of(Festival.archived("opened","Opened"),Festival.active("next","Next")));
        assertThrows(DomainValidationException.class,()->view.save("member",band,Rating.of(1)));
        view.findByUserAndBand("member",band);
        verify(planning).findByUserFestivalAndBand("member","opened",band);
        verifyNoMoreInteractions(planning);
    }
    @Test void removedLineupRejectsPlanningAndAfterViewReadsOnlyExplicitFestival() {
        var festivals=mock(FestivalRepository.class);
        var lineup=mock(FestivalLineupRepository.class);
        var planning=mock(FestivalPlanningRatingRepository.class);
        var after=mock(FestivalAfterRatingRepository.class);
        var band=new Band("Known");
        when(festivals.findAll()).thenReturn(List.of(Festival.active("opened","Opened")));
        when(lineup.findByFestival("opened")).thenReturn(List.of());
        var view=new FestivalPlanningRatingView(festivals,lineup,planning,"group","opened");
        assertThrows(DomainValidationException.class,()->view.save("member",band,Rating.of(1)));
        verifyNoInteractions(planning);
        var scoped=new FestivalAfterRatingView(after,"opened");
        assertTrue(scoped.findByUserAndBand("member",band).isEmpty());
        verify(after).findByUserFestivalAndBand("member","opened",band);
        assertThrows(UnsupportedOperationException.class,()->scoped.save("member",band,Rating.of(2)));
    }
}
