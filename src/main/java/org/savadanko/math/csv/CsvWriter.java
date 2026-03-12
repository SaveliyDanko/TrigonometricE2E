package org.savadanko.math.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class CsvWriter {
    public void writeResults(Path outputFile, List<CsvResultRow> rows) throws IOException {
        if (outputFile.getParent() != null) {
            Files.createDirectories(outputFile.getParent());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("x,result").append(System.lineSeparator());

        for (CsvResultRow row : rows) {
            builder.append(format(row.x()))
                    .append(",")
                    .append(format(row.result()))
                    .append(System.lineSeparator());
        }

        Files.writeString(outputFile, builder.toString());
    }

    public void writeInput(Path outputFile, List<Double> values) throws IOException {
        if (outputFile.getParent() != null) {
            Files.createDirectories(outputFile.getParent());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("x").append(System.lineSeparator());

        for (Double value : values) {
            builder.append(format(value)).append(System.lineSeparator());
        }

        Files.writeString(outputFile, builder.toString());
    }

    private String format(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        }
        if (Double.isInfinite(value)) {
            return value > 0 ? "Infinity" : "-Infinity";
        }
        return Double.toString(value);
    }
}
