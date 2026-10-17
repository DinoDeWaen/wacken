package be.wacken.planner;

import java.io.IOException;
import java.util.List;

import be.wacken.planner.domain.PersonalBandRatingEvent;

interface SupabasePersonalBandRatingRemote {
    void pushEvent(AuthSession session, PersonalBandRatingEvent event) throws IOException;

    List<PersonalBandRatingEvent> pullUserEvents(AuthSession session) throws IOException;
}
