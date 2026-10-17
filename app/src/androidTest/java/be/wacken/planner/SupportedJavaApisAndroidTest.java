package be.wacken.planner;

import androidx.test.platform.app.InstrumentationRegistry;
import be.wacken.planner.domain.*;
import be.wacken.planner.infrastructure.FileBackedBandRepository;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public final class SupportedJavaApisAndroidTest {
    @Test public void appPrivateTsvStorageRoundTripsMetadataWithoutChangingExistingHistory() throws Exception {
        var context=InstrumentationRegistry.getInstrumentation().getTargetContext();
        Path root=Files.createTempDirectory(context.getFilesDir().toPath(),"compatibility ü ");
        try {
            Path history=root.resolve("personal-history.tsv");Files.write(history,"member\tBjörk\t4".getBytes(StandardCharsets.UTF_8));
            var repository=new FileBackedBandRepository(root);
            Band band=new Band("Björk & 雪",Optional.of("Original biography"),Optional.empty(),Optional.empty(),Optional.empty());
            repository.save(band);
            assertEquals(Optional.of(band),new FileBackedBandRepository(root).findByName(band.name()));
            assertEquals("member\tBjörk\t4",new String(Files.readAllBytes(history),StandardCharsets.UTF_8));
        } finally {
            try(var paths=Files.walk(root)) {
                for(Path path:paths.sorted(Comparator.reverseOrder()).collect(java.util.stream.Collectors.toList()))Files.deleteIfExists(path);
            }
        }
    }
    @Test public void actualMusicLookupUsesCompatibleUtf8QueryEncoding() throws Exception {
        List<String> requests=new ArrayList<>();
        var provider=new YouTubeMetadataProvider("key&+",path->{requests.add(path);return "{\"items\":[]}";});
        assertTrue(provider.search("Björk & 雪/+?").isEmpty());
        assertEquals(List.of("/search?part=snippet&q=Bj%C3%B6rk+%26+%E9%9B%AA%2F%2B%3F&type=channel&maxResults=5&key=key%26%2B"),requests);
    }
}
