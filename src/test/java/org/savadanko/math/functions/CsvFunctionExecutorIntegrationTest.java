package org.savadanko.math.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.savadanko.math.logger.AppLogger;
import org.savadanko.math.utils.FunctionFactory;
import org.savadanko.math.utils.ModuleType;
import org.savadanko.math.csv.CsvFunctionExecutor;
import org.savadanko.math.csv.CsvReader;
import org.savadanko.math.csv.CsvWriter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class CsvFunctionExecutorIntegrationTest {
    private static final double EPS = 1e-10;
    private static final int MAX_ITERATIONS = 100_000;

    @TempDir
    Path tempDir;

    @Test
    void shouldRunCosModuleFromCsvAndWriteOutputAndLog() throws Exception {
        Path inputFile = tempDir.resolve("cos-input.csv");
        Path outputFile = tempDir.resolve("cos-output.csv");
        Path logFile = tempDir.resolve("app.log");

        Files.write(inputFile, List.of(
                "x",
                "0.0",
                "1.0",
                "2.0"
        ));

        Logger logger = AppLogger.configure(logFile);
        FunctionFactory factory = new FunctionFactory(EPS, MAX_ITERATIONS, logger);
        CsvFunctionExecutor executor = new CsvFunctionExecutor(new CsvReader(), new CsvWriter(), factory);

        executor.execute(ModuleType.COS, inputFile, outputFile);

        assertTrue(Files.exists(outputFile));
        List<String> outputLines = Files.readAllLines(outputFile);
        assertEquals("x,result", outputLines.get(0));
        assertEquals(4, outputLines.size());

        String outputText = Files.readString(outputFile);
        assertTrue(outputText.contains("0.0,1.0"));

        assertTrue(Files.exists(logFile));
        String logText = Files.readString(logFile);
        assertTrue(logText.contains("module=cos"));
        assertTrue(logText.contains("input=0.0"));
        assertTrue(logText.contains("result=1.0"));
    }

    @Test
    void shouldRunWholeSystemFromCsvAndWriteSystemLogs() throws Exception {
        Path inputFile = tempDir.resolve("system-input.csv");
        Path outputFile = tempDir.resolve("system-output.csv");
        Path logFile = tempDir.resolve("system.log");

        Files.write(inputFile, List.of(
                "x",
                "-2.0",
                "-1.0",
                "0.25",
                "2.0"
        ));

        Logger logger = AppLogger.configure(logFile);
        FunctionFactory factory = new FunctionFactory(EPS, MAX_ITERATIONS, logger);
        CsvFunctionExecutor executor = new CsvFunctionExecutor(new CsvReader(), new CsvWriter(), factory);

        executor.execute(ModuleType.SYSTEM, inputFile, outputFile);

        assertTrue(Files.exists(outputFile));
        assertTrue(Files.readAllLines(outputFile).size() >= 2);

        String logText = Files.readString(logFile);
        assertTrue(logText.contains("module=system"));
    }
}
