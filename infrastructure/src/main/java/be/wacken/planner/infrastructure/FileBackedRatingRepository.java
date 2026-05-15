package be.wacken.planner.infrastructure;

import be.wacken.planner.domain.Band;
import be.wacken.planner.domain.Rating;
import be.wacken.planner.domain.RatingRepository;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class FileBackedRatingRepository implements RatingRepository {
    private final FileBackedStorage storage;

    public FileBackedRatingRepository(Path directory) {
        this.storage = new FileBackedStorage(directory, "ratings.tsv");
    }

    @Override
    public void save(String userName, Band band, Rating rating) {
        Map<RatingKey, RatingEntry> ratingsByKey = loadRatingsByKey();
        ratingsByKey.put(new RatingKey(userName, band.name()), new RatingEntry(userName, band.name(), rating));
        storage.writeRows(ratingsByKey.values().stream()
                .map(entry -> java.util.Arrays.asList(entry.userName(), entry.bandName(), Integer.toString(entry.rating().value())))
                .collect(Collectors.toList()));
    }

    @Override
    public Optional<Rating> findByUserAndBand(String userName, Band band) {
        return Optional.ofNullable(loadRatingsByKey().get(new RatingKey(userName, band.name())))
                .map(RatingEntry::rating);
    }

    private Map<RatingKey, RatingEntry> loadRatingsByKey() {
        Map<RatingKey, RatingEntry> ratingsByKey = new LinkedHashMap<>();
        for (List<String> row : storage.readRows()) {
            RatingEntry entry = new RatingEntry(row.get(0), row.get(1), Rating.of(Integer.parseInt(row.get(2))));
            ratingsByKey.put(new RatingKey(entry.userName(), entry.bandName()), entry);
        }
        return ratingsByKey;
    }

    private record RatingKey(String userName, String bandName) {
    }

    private record RatingEntry(String userName, String bandName, Rating rating) {
    }
}
