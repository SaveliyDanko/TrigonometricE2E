package org.savadanko.math.csv;

import org.savadanko.math.utils.ModuleType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class SampleCsvGenerator {
    private final CsvWriter csvWriter;
    private final CsvFunctionExecutor executor;

    public SampleCsvGenerator(CsvWriter csvWriter, CsvFunctionExecutor executor) {
        this.csvWriter = csvWriter;
        this.executor = executor;
    }

    public void generate(Path baseDirectory) throws IOException {
        Path inputDir = baseDirectory.resolve("input");
        Path outputDir = baseDirectory.resolve("output");

        Path cosInput = inputDir.resolve("cos-input.csv");
        Path ctgInput = inputDir.resolve("ctg-input.csv");
        Path systemInput = inputDir.resolve("system-input.csv");

        csvWriter.writeInput(cosInput, List.of(
                -3.0, -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0, 3.0
        ));

        csvWriter.writeInput(ctgInput, List.of(
                -2.5, -2.0, -1.5, -1.0, -0.5, 0.5, 1.0, 1.5, 2.0, 2.5
        ));

        csvWriter.writeInput(systemInput, List.of(
                -3.0, -2.0, -1.0, -0.5, -0.25, 0.25, 0.5, 1.0, 2.0, 3.0, 10.0
        ));

        executor.execute(ModuleType.COS, cosInput, outputDir.resolve("cos-output.csv"));
        executor.execute(ModuleType.COT, ctgInput, outputDir.resolve("ctg-output.csv"));
        executor.execute(ModuleType.SYSTEM, systemInput, outputDir.resolve("system-output.csv"));
    }
}
