package be.wacken.planner;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

public final class AndroidRuntimeCompatibilityRegressionTest {
    private static final Pattern DIRECT_STREAM_TO_LIST = Pattern.compile("(?<!Collectors)\\.toList\\s*\\(");

    @Test
    public void productionCodeAvoidsDirectStreamToListOnAndroidRuntimePaths() throws IOException {
        List<String> violations = new ArrayList<>();
        for (Path sourceRoot : sourceRoots()) {
            if (!Files.exists(sourceRoot)) {
                continue;
            }
            try (var files = Files.walk(sourceRoot)) {
                files.filter(path -> path.toString().endsWith(".java"))
                        .forEach(path -> collectViolations(path, violations));
            }
        }

        assertTrue("Direct Stream.toList() is not safe for current Android runtime paths: " + violations, violations.isEmpty());
    }

    private List<Path> sourceRoots() {
        return List.of(
                Path.of("app/src/main/java"),
                Path.of("application/src/main/java"),
                Path.of("domain/src/main/java"),
                Path.of("infrastructure/src/main/java")
        );
    }

    private void collectViolations(Path path, List<String> violations) {
        try {
            String source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            if (DIRECT_STREAM_TO_LIST.matcher(source).find()) {
                violations.add(path.toString());
            }
        } catch (IOException error) {
            throw new IllegalStateException("Could not inspect " + path, error);
        }
    }
}
