package org.savadanko.math.functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.savadanko.math.csv.CsvFunctionExecutor;
import org.savadanko.math.csv.CsvReader;
import org.savadanko.math.csv.CsvWriter;
import org.savadanko.math.utils.FunctionFactory;
import org.savadanko.math.utils.ModuleType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TabularValidationIntegrationTest extends IntegrationTestSupport {

    @TempDir
    Path tempDir;

    private CsvFunctionExecutor executorWithValidation;
    private CsvFunctionExecutor executorWithoutValidation;

    @BeforeEach
    void setUp() {
        CsvReader csvReader = new CsvReader();
        CsvWriter csvWriter = new CsvWriter();
        FunctionFactory functionFactory = new FunctionFactory(EPS, MAX_ITERATIONS);

        executorWithValidation = new CsvFunctionExecutor(csvReader, csvWriter, functionFactory, true);
        executorWithoutValidation = new CsvFunctionExecutor(csvReader, csvWriter, functionFactory, false);
    }

    @Test
    void shouldExecuteWithTabularValidationEnabled() throws IOException {
        Path inputFile = tempDir.resolve("sin_tabular_input.csv");
        Path outputFile = tempDir.resolve("sin_tabular_output.csv");

        List<String> inputLines = Arrays.asList(
            "x",
            "0.0",                              // sin(0) = 0
            String.valueOf(Math.PI/6),          // sin(π/6) = 0.5
            String.valueOf(Math.PI/4),          // sin(π/4) = √2/2
            String.valueOf(Math.PI/2),          // sin(π/2) = 1
            "1.5"                               
        );

        Files.write(inputFile, inputLines);

        assertDoesNotThrow(() -> executorWithValidation.execute(ModuleType.SIN, inputFile, outputFile));

        assertTrue(Files.exists(outputFile));

        List<String> outputLines = Files.readAllLines(outputFile);
        assertTrue(outputLines.size() >= 5); 
        assertEquals("x,result", outputLines.get(0));

        String[] firstDataLine = outputLines.get(1).split(",");
        assertEquals("0.0", firstDataLine[0]);
        assertEquals("0.0", firstDataLine[1]);
    }

    @Test
    void shouldExecuteForCosWithTabularValidation() throws IOException {
        Path inputFile = tempDir.resolve("cos_input.csv");
        Path outputFile = tempDir.resolve("cos_output.csv");

        List<String> inputLines = Arrays.asList(
            "x",
            "0.0",                              // cos(0) = 1
            String.valueOf(Math.PI/3),          // cos(π/3) = 0.5
            String.valueOf(Math.PI/2),          // cos(π/2) = 0
            String.valueOf(Math.PI)             // cos(π) = -1
        );

        Files.write(inputFile, inputLines);

        assertDoesNotThrow(() -> executorWithValidation.execute(ModuleType.COS, inputFile, outputFile));
        assertTrue(Files.exists(outputFile));

        List<String> outputLines = Files.readAllLines(outputFile);
        assertTrue(outputLines.size() >= 4);
    }

    @Test
    void shouldExecuteForTanWithTabularValidation() throws IOException {
        Path inputFile = tempDir.resolve("tan_input.csv");
        Path outputFile = tempDir.resolve("tan_output.csv");

        List<String> inputLines = Arrays.asList(
            "x",
            "0.0",                              // tan(0) = 0
            String.valueOf(Math.PI/4),          // tan(π/4) = 1
            String.valueOf(Math.PI/6)           // tan(π/6) = 1/√3
        );

        Files.write(inputFile, inputLines);

        assertDoesNotThrow(() -> executorWithValidation.execute(ModuleType.TAN, inputFile, outputFile));
        assertTrue(Files.exists(outputFile));
    }

    @Test
    void shouldExecuteForLnWithTabularValidation() throws IOException {
        Path inputFile = tempDir.resolve("ln_input.csv");
        Path outputFile = tempDir.resolve("ln_output.csv");

        List<String> inputLines = Arrays.asList(
            "x",
            "1.0",                              // ln(1) = 0
            String.valueOf(Math.E),             // ln(e) = 1
            "2.0",                              // ln(2) = ln(2)
            "0.5"                               // ln(0.5) = -ln(2)
        );

        Files.write(inputFile, inputLines);

        assertDoesNotThrow(() -> executorWithValidation.execute(ModuleType.LN, inputFile, outputFile));
        assertTrue(Files.exists(outputFile));
    }

    @Test
    void shouldExecuteWithoutValidationForUnsupportedModules() throws IOException {
        Path inputFile = tempDir.resolve("cot_input.csv");
        Path outputFile = tempDir.resolve("cot_output.csv");

        List<String> inputLines = Arrays.asList(
            "x",
            "1.0",
            "0.5"
        );

        Files.write(inputFile, inputLines);

        assertDoesNotThrow(() -> executorWithValidation.execute(ModuleType.COT, inputFile, outputFile));
        assertTrue(Files.exists(outputFile));
    }

    @Test
    void shouldExecuteWithoutValidationWhenDisabled() throws IOException {
        Path inputFile = tempDir.resolve("sin_input.csv");
        Path outputFile = tempDir.resolve("sin_output.csv");

        List<String> inputLines = Arrays.asList(
            "x",
            "0.0",
            "1.0"
        );

        Files.write(inputFile, inputLines);

        assertDoesNotThrow(() -> executorWithoutValidation.execute(ModuleType.SIN, inputFile, outputFile));
        assertTrue(Files.exists(outputFile));
    }

    @Test
    void shouldHandleEmptyInputFile() throws IOException {
        Path inputFile = tempDir.resolve("empty_input.csv");
        Path outputFile = tempDir.resolve("empty_output.csv");

        Files.write(inputFile, Arrays.asList("x"));

        assertDoesNotThrow(() -> executorWithValidation.execute(ModuleType.SIN, inputFile, outputFile));
        assertTrue(Files.exists(outputFile));

        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals(1, outputLines.size()); 
        assertEquals("x,result", outputLines.get(0));
    }

    @Test
    void shouldPreserveOutputFormatWithValidation() throws IOException {
        Path inputFile = tempDir.resolve("format_test_input.csv");
        Path outputFile1 = tempDir.resolve("output_with_validation.csv");
        Path outputFile2 = tempDir.resolve("output_without_validation.csv");

        List<String> inputLines = Arrays.asList(
            "x",
            "0.0",
            "1.0"
        );

        Files.write(inputFile, inputLines);

        executorWithValidation.execute(ModuleType.SIN, inputFile, outputFile1);
        executorWithoutValidation.execute(ModuleType.SIN, inputFile, outputFile2);

        List<String> output1 = Files.readAllLines(outputFile1);
        List<String> output2 = Files.readAllLines(outputFile2);

        assertEquals(output1.size(), output2.size());
        assertEquals(output1.get(0), output2.get(0)); 

        for (int i = 1; i < output1.size(); i++) {
            String[] values1 = output1.get(i).split(",");
            String[] values2 = output2.get(i).split(",");

            assertEquals(values1[0], values2[0]); 

            double result1 = Double.parseDouble(values1[1]);
            double result2 = Double.parseDouble(values2[1]);
            assertEquals(result1, result2, 1e-15); 
        }
    }
}