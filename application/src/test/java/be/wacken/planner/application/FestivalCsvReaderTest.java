package be.wacken.planner.application;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FestivalCsvReaderTest {
    @Test
    void preservesQuotedLineBreaksAndEscapedQuotes() {
        List<FestivalCsvReader.Row> rows = FestivalCsvReader.rows(
                "bands.csv",
                "band_id,name,biography\r\nfirst,\"AC, DC\",\"First line\r\nSecond \"\"line\"\"\"\r\n"
        );

        assertEquals(1, rows.size());
        assertEquals(2, rows.get(0).rowNumber());
        assertEquals("AC, DC", rows.get(0).value("name"));
        assertEquals("First line\r\nSecond \"line\"", rows.get(0).value("biography"));
    }

    @Test
    void bandOnlyImportUsesTheSameCsvRules() {
        assertEquals(List.of("AC, DC", "Band \"Two\""), FestivalCsvReader.bandNames(
                "band_id,name\nfirst,\"AC, DC\"\nsecond,\"Band \"\"Two\"\"\"\n"
        ));
    }

    @Test
    void skipsBlankLinesWithoutDiscardingLineBreaksInsideQuotes() {
        List<FestivalCsvReader.Row> rows = FestivalCsvReader.rows(
                "bands.csv", "band_id,name,biography\n \nfirst,First,\"Line one\n\nLine three\"\n"
        );

        assertEquals(1, rows.size());
        assertEquals(3, rows.get(0).rowNumber());
        assertEquals("Line one\n\nLine three", rows.get(0).value("biography"));
    }

    @Test
    void rejectsMalformedQuotingWithFileAndLine() {
        FestivalCsvReader.FormatException error = assertThrows(
                FestivalCsvReader.FormatException.class,
                () -> FestivalCsvReader.rows("bands.csv", "band_id,name\nfirst,\"Open\n")
        );

        assertEquals("bands.csv row 2: unterminated quoted field", error.getMessage());
    }

    @Test
    void bandOnlyImportRejectsMissingNameHeaderBeforeWriting() {
        assertThrows(FestivalCsvReader.FormatException.class,
                () -> FestivalCsvReader.bandNames("band_id,title\nfirst,Airbourne\n"));
    }

    @Test
    void bandOnlyImportRejectsBlankNamesInsteadOfSilentlyDroppingRows() {
        assertThrows(FestivalCsvReader.FormatException.class,
                () -> FestivalCsvReader.bandNames("band_id,name\nfirst,Airbourne\nsecond,\n"));
    }
}
