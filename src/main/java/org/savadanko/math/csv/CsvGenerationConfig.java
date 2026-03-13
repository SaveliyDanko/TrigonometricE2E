package org.savadanko.math.csv;

import org.savadanko.math.utils.ModuleType;


public record CsvGenerationConfig(
        ModuleType moduleType,
        double startX,
        double endX,
        double step,
        String separator
) {
    public CsvGenerationConfig {
        if (step <= 0) {
            throw new IllegalArgumentException("Step must be positive, got: " + step);
        }
        if (startX > endX) {
            throw new IllegalArgumentException(
                    String.format("Start X (%.6f) must be <= End X (%.6f)", startX, endX)
            );
        }
        if (separator == null || separator.isEmpty()) {
            throw new IllegalArgumentException("Separator cannot be null or empty");
        }
    }


    public static CsvGenerationConfig withDefaultSeparator(
            ModuleType moduleType,
            double startX,
            double endX,
            double step
    ) {
        return new CsvGenerationConfig(moduleType, startX, endX, step, ",");
    }
}