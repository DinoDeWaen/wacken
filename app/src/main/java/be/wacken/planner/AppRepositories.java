package be.wacken.planner;

import android.content.Context;

import java.nio.file.Path;

import be.wacken.planner.infrastructure.FileBackedBandRepository;
import be.wacken.planner.infrastructure.FileBackedPerformanceRepository;
import be.wacken.planner.infrastructure.FileBackedRatingRepository;

final class AppRepositories {
    private final Path storageDirectory;

    AppRepositories(Context context) {
        this.storageDirectory = context.getFilesDir().toPath();
    }

    FileBackedBandRepository bands() {
        return new FileBackedBandRepository(storageDirectory);
    }

    FileBackedPerformanceRepository performances() {
        return new FileBackedPerformanceRepository(storageDirectory);
    }

    FileBackedRatingRepository ratings() {
        return new FileBackedRatingRepository(storageDirectory);
    }
}
