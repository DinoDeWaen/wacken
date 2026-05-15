package be.wacken.planner.infrastructure;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

final class FileBackedStorage {
    private static final String COLUMN_SEPARATOR = "\t";

    private final Path file;

    FileBackedStorage(Path directory, String fileName) {
        this.file = directory.resolve(fileName);
    }

    List<List<String>> readRows() {
        if (Files.notExists(file)) {
            return List.of();
        }
        try {
            return Files.readAllLines(file, StandardCharsets.UTF_8)
                    .stream()
                    .filter(line -> !line.isBlank())
                    .map(this::decodeRow)
                    .toList();
        } catch (IOException error) {
            throw new IllegalStateException("Could not read MVP local storage.", error);
        }
    }

    void writeRows(List<List<String>> rows) {
        try {
            Files.createDirectories(file.getParent());
            Files.write(
                    file,
                    rows.stream().map(this::encodeRow).toList(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException error) {
            throw new IllegalStateException("Could not write MVP local storage.", error);
        }
    }

    private String encodeRow(List<String> row) {
        return row.stream()
                .map(FileBackedStorage::encode)
                .reduce((left, right) -> left + COLUMN_SEPARATOR + right)
                .orElse("");
    }

    private List<String> decodeRow(String row) {
        return List.of(row.split(COLUMN_SEPARATOR, -1))
                .stream()
                .map(FileBackedStorage::decode)
                .toList();
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
