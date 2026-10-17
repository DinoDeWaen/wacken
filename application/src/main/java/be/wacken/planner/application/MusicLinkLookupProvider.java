package be.wacken.planner.application;

import java.util.List;

/** Music-only evidence lookup alongside the existing general metadata providers. */
public interface MusicLinkLookupProvider {
    Lookup lookup(String searchTerm) throws BandMetadataLookupException;
    record Lookup(List<MusicLinkEvidence> candidates,List<String> messages,boolean retryable){
        public Lookup(List<MusicLinkEvidence> candidates,List<String> messages){this(candidates,messages,false);}
        public Lookup {candidates=List.copyOf(candidates);messages=List.copyOf(messages);}
    }
}
