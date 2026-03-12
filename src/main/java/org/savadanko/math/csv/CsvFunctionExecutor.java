package org.savadanko.math.csv;

import org.savadanko.math.utils.FunctionFactory;
import org.savadanko.math.utils.ModuleType;
import org.savadanko.math.functions.UnaryFunction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvFunctionExecutor {
    private final CsvReader csvReader;
    private final CsvWriter csvWriter;
    private final FunctionFactory functionFactory;

    public CsvFunctionExecutor(CsvReader csvReader, CsvWriter csvWriter, FunctionFactory functionFactory) {
        this.csvReader = csvReader;
        this.csvWriter = csvWriter;
        this.functionFactory = functionFactory;
    }

    public void execute(ModuleType moduleType, Path inputFile, Path outputFile) throws IOException {
        UnaryFunction function = functionFactory.create(moduleType);
        List<Double> values = csvReader.read(inputFile);

        List<CsvResultRow> results = new ArrayList<>();
        for (double x : values) {
            results.add(new CsvResultRow(x, function.apply(x)));
        }

        csvWriter.writeResults(outputFile, results);
    }
}
