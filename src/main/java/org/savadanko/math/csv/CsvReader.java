package org.savadanko.math.csv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvReader {
    public List<Double> read(Path inputFile) throws IOException {
        List<String> lines = Files.readAllLines(inputFile);
        List<Double> values = new ArrayList<>();

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String firstCell = line.split(";|,")[0].trim();

            if (firstCell.equalsIgnoreCase("x")) {
                continue;
            }

            values.add(Double.parseDouble(firstCell));
        }

        return values;
    }
}
