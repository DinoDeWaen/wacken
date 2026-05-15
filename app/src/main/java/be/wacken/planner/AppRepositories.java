package be.wacken.planner;

import android.content.Context;

import java.nio.file.Path;

import be.wacken.planner.infrastructure.FileBackedBandRepository;
import be.wacken.planner.infrastructure.FileBackedFoodOptionRepository;
import be.wacken.planner.infrastructure.FileBackedPerformanceRepository;
import be.wacken.planner.infrastructure.FileBackedRatingRepository;
import be.wacken.planner.infrastructure.FileBackedStageDistanceRepository;
import be.wacken.planner.infrastructure.FileBackedStageRepository;

final class AppRepositories {
    private final Path storageDirectory;

    AppRepositories(Context context) {
        this.storageDirectory = context.getFilesDir().toPath();
    }

    FileBackedBandRepository bands() {
        return new FileBackedBandRepository(storageDirectory);
    }

    FileBackedStageRepository stages() {
        return new FileBackedStageRepository(storageDirectory);
    }

    FileBackedPerformanceRepository performances() {
        return new FileBackedPerformanceRepository(storageDirectory);
    }

    FileBackedStageDistanceRepository distances() {
        return new FileBackedStageDistanceRepository(storageDirectory);
    }

    FileBackedFoodOptionRepository foodOptions() {
        return new FileBackedFoodOptionRepository(storageDirectory);
    }

    FileBackedRatingRepository ratings() {
        return new FileBackedRatingRepository(storageDirectory);
    }
}
