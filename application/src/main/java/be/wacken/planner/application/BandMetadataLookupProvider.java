package be.wacken.planner.application;

import java.util.List;

public interface BandMetadataLookupProvider {
    String name();

    boolean configured();

    List<BandMetadataProviderCandidate> search(String bandName) throws BandMetadataLookupException;
}
