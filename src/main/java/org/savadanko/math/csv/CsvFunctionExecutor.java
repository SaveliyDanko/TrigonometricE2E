package org.savadanko.math.csv;

import org.savadanko.math.utils.FunctionFactory;
import org.savadanko.math.utils.ModuleType;
import org.savadanko.math.utils.TabularValidator;
import org.savadanko.math.functions.UnaryFunction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class CsvFunctionExecutor {
    private static final Logger logger = Logger.getLogger(CsvFunctionExecutor.class.getName());

    private final CsvReader csvReader;
    private final CsvWriter csvWriter;
    private final FunctionFactory functionFactory;
    private final boolean enableTabularValidation;

    public CsvFunctionExecutor(CsvReader csvReader, CsvWriter csvWriter, FunctionFactory functionFactory) {
        this(csvReader, csvWriter, functionFactory, true);
    }

    public CsvFunctionExecutor(CsvReader csvReader, CsvWriter csvWriter, FunctionFactory functionFactory, boolean enableTabularValidation) {
        this.csvReader = csvReader;
        this.csvWriter = csvWriter;
        this.functionFactory = functionFactory;
        this.enableTabularValidation = enableTabularValidation;
    }

    public void execute(ModuleType moduleType, Path inputFile, Path outputFile) throws IOException {
        logger.info("Starting execution for module: " + moduleType + ", input: " + inputFile + ", output: " + outputFile);

        UnaryFunction function = functionFactory.create(moduleType);
        List<Double> values = csvReader.read(inputFile);
        logger.info("Read " + values.size() + " input values from CSV");

        List<CsvResultRow> results = new ArrayList<>();
        for (double x : values) {
            double y = function.apply(x);
            results.add(new CsvResultRow(x, y));
        }

        if (enableTabularValidation) {
            performTabularValidation(moduleType, results);
        }

        csvWriter.writeResults(outputFile, results);
        logger.info("Execution completed successfully");
    }

    private void performTabularValidation(ModuleType moduleType, List<CsvResultRow> results) {
        int tabularValuesCount = TabularValidator.getTabularValuesCount(moduleType);

        if (tabularValuesCount == 0) {
            logger.info("No tabular validation data available for module " + moduleType);
            return;
        }

        logger.info("Starting tabular validation for " + moduleType +
                   " (available " + tabularValuesCount + " reference values)");

        List<Double> xValues = new ArrayList<>();
        List<Double> yValues = new ArrayList<>();

        for (CsvResultRow result : results) {
            xValues.add(result.x());
            yValues.add(result.result());
        }

        List<TabularValidator.ValidationResult> validationResults =
                TabularValidator.validateValues(moduleType, xValues, yValues);

        int validatedCount = 0;
        int errorCount = 0;

        for (TabularValidator.ValidationResult validationResult : validationResults) {
            if (validationResult.isValid() && !Double.isNaN(validationResult.getExpectedValue())) {
                validatedCount++;
                logger.fine("Tabular validation passed: " + validationResult);
            } else if (!validationResult.isValid()) {
                errorCount++;
                logger.warning("Tabular validation FAILED: " + validationResult.getErrorMessage());
            }
        }

        if (validatedCount > 0) {
            logger.info("Tabular validation completed: " + validatedCount + " values validated successfully" +
                       (errorCount > 0 ? ", " + errorCount + " validation errors found" : ""));
        }

        if (errorCount > 0) {
            logger.severe("TABULAR VALIDATION ERRORS: " + errorCount + " values failed validation for " + moduleType);
        }
    }
}
