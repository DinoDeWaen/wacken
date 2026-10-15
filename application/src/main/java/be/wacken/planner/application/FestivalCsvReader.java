package be.wacken.planner.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class FestivalCsvReader {
    private FestivalCsvReader() {
    }

    public static List<Row> rows(String fileName, String csv) {
        Objects.requireNonNull(fileName, "fileName must not be null");
        Objects.requireNonNull(csv, "csv must not be null");
        List<Record> records = records(fileName, csv);
        if (records.isEmpty()) {
            return List.of();
        }

        List<String> headers = records.get(0).values();
        List<Row> rows = new ArrayList<>();
        for (int index = 1; index < records.size(); index++) {
            Record record = records.get(index);
            Map<String, String> values = new LinkedHashMap<>();
            for (int column = 0; column < headers.size(); column++) {
                values.put(headers.get(column).trim(),
                        column < record.values().size() ? record.values().get(column).trim() : "");
            }
            rows.add(new Row(record.lineNumber(), values));
        }
        return rows;
    }

    public static List<String> bandNames(String csv) {
        return rows("bands.csv", csv).stream()
                .map(row -> row.valuesByHeader().containsKey("name") ? row.value("name") : row.firstValue())
                .filter(name -> !name.isBlank())
                .toList();
    }

    private static List<Record> records(String fileName, String csv) {
        List<Record> records = new ArrayList<>();
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        State state = State.START;
        int line = 1;
        int recordLine = 1;
        for (int index = 0; index < csv.length(); index++) {
            char character = csv.charAt(index);
            if (index == 0 && character == '\uFEFF') {
                continue;
            }
            if (state == State.QUOTED) {
                if (character == '"') {
                    if (index + 1 < csv.length() && csv.charAt(index + 1) == '"') {
                        value.append('"');
                        index++;
                    } else {
                        state = State.AFTER_QUOTE;
                    }
                } else {
                    value.append(character);
                    if (character == '\n' || (character == '\r' && (index + 1 == csv.length() || csv.charAt(index + 1) != '\n'))) {
                        line++;
                    }
                }
                continue;
            }
            if (character == ',' || character == '\n' || character == '\r') {
                if (character == ',' || state != State.START || !values.isEmpty() || !value.isEmpty()) {
                    values.add(value.toString());
                    value.setLength(0);
                }
                state = State.START;
                if (character != ',') {
                    if (!values.isEmpty()) {
                        if (values.size() > 1 || !values.get(0).isBlank()) {
                            records.add(new Record(recordLine, List.copyOf(values)));
                        }
                        values.clear();
                    }
                    if (character == '\r' && index + 1 < csv.length() && csv.charAt(index + 1) == '\n') {
                        index++;
                    }
                    line++;
                    recordLine = line;
                }
            } else if (character == '"') {
                if (state == State.START) {
                    state = State.QUOTED;
                } else {
                    throw new FormatException(fileName, line, "unexpected quote");
                }
            } else if (state == State.AFTER_QUOTE) {
                throw new FormatException(fileName, line, "unexpected character after closing quote");
            } else {
                value.append(character);
                state = State.UNQUOTED;
            }
        }
        if (state == State.QUOTED) {
            throw new FormatException(fileName, recordLine, "unterminated quoted field");
        }
        if (state != State.START || !values.isEmpty() || !value.isEmpty()) {
            values.add(value.toString());
            if (values.size() > 1 || !values.get(0).isBlank()) {
                records.add(new Record(recordLine, List.copyOf(values)));
            }
        }
        return records;
    }

    public record Row(int rowNumber, Map<String, String> valuesByHeader) {
        public Row {
            valuesByHeader = Collections.unmodifiableMap(new LinkedHashMap<>(valuesByHeader));
        }

        public String value(String header) {
            return valuesByHeader.getOrDefault(header, "");
        }

        public String required(String header) {
            return value(header);
        }

        public String firstValue() {
            return valuesByHeader.values().stream().findFirst().orElse("");
        }
    }

    public static final class FormatException extends IllegalArgumentException {
        public FormatException(String fileName, int line, String detail) {
            super(fileName + " row " + line + ": " + detail);
        }
    }

    private enum State {
        START, UNQUOTED, QUOTED, AFTER_QUOTE
    }

    private record Record(int lineNumber, List<String> values) {
    }
}
