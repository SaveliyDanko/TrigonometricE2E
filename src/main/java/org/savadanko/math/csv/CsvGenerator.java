package org.savadanko.math.csv;

import org.savadanko.math.functions.UnaryFunction;
import org.savadanko.math.utils.FunctionFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public final class CsvGenerator {

    private final FunctionFactory functionFactory;

    public CsvGenerator(double eps, int maxIterations, Logger logger) {
        this.functionFactory = new FunctionFactory(eps, maxIterations, logger);
    }

    public void generateCsv(CsvGenerationConfig config, Path outputFile) throws IOException {
        List<Double> xValues = generateXValues(config.startX(), config.endX(), config.step());

        UnaryFunction function = functionFactory.create(config.moduleType());

        List<CsvResultRow> results = new ArrayList<>();
        for (Double x : xValues) {
            double result = function.apply(x);
            results.add(new CsvResultRow(x, result));
        }

        writeCsvWithSeparator(outputFile, results, config.separator());
    }

   
    private List<Double> generateXValues(double start, double end, double step) {
        List<Double> values = new ArrayList<>();
        int numSteps = (int) Math.ceil((end - start) / step);

        for (int i = 0; i <= numSteps; i++) {
            double x = start + i * step;
            if (x <= end) {
                values.add(x);
            }
        }

        return values;
    }

 
    private void writeCsvWithSeparator(Path outputFile, List<CsvResultRow> rows, String separator)
            throws IOException {
        if (outputFile.getParent() != null) {
            Files.createDirectories(outputFile.getParent());
        }

        StringBuilder builder = new StringBuilder();
        builder.append("x").append(separator).append("result").append(System.lineSeparator());

        for (CsvResultRow row : rows) {
            builder.append(formatValue(row.x()))
                    .append(separator)
                    .append(formatValue(row.result()))
                    .append(System.lineSeparator());
        }

        Files.writeString(outputFile, builder.toString());
    }

  
    private String formatValue(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        }
        if (Double.isInfinite(value)) {
            return value > 0 ? "Infinity" : "-Infinity";
        }
        return Double.toString(value);
    }

   
    public void generateCsv(
            String moduleName,
            double startX,
            double endX,
            double step,
            Path outputFile
    ) throws IOException {
        var moduleType = org.savadanko.math.utils.ModuleType.fromString(moduleName);
        var config = CsvGenerationConfig.withDefaultSeparator(moduleType, startX, endX, step);
        generateCsv(config, outputFile);
    }
}