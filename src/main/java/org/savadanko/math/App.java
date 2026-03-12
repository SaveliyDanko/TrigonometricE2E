package org.savadanko.math;

import org.savadanko.math.csv.CsvFunctionExecutor;
import org.savadanko.math.csv.CsvReader;
import org.savadanko.math.csv.CsvWriter;
import org.savadanko.math.csv.SampleCsvGenerator;
import org.savadanko.math.logger.AppLogger;
import org.savadanko.math.utils.FunctionFactory;
import org.savadanko.math.utils.ModuleType;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public final class App {
    private static final double EPS = 1e-10;
    private static final int MAX_ITERATIONS = 100_000;

    private App() {
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            printUsage();
            return;
        }

        if ("samples".equalsIgnoreCase(args[0])) {
            runSampleGeneration(args);
            return;
        }

        runModule(args);
    }

    private static void runSampleGeneration(String[] args) throws IOException {
        Path samplesDir = Path.of(args.length >= 2 ? args[1] : "samples");
        Path logFile = Path.of(args.length >= 3 ? args[2] : "logs/app.log");

        Logger logger = AppLogger.configure(logFile);
        FunctionFactory factory = new FunctionFactory(EPS, MAX_ITERATIONS, logger);
        CsvFunctionExecutor executor = new CsvFunctionExecutor(new CsvReader(), new CsvWriter(), factory);
        SampleCsvGenerator generator = new SampleCsvGenerator(new CsvWriter(), executor);

        generator.generate(samplesDir);

        System.out.println("Sample CSV files generated in: " + samplesDir.toAbsolutePath());
        System.out.println("Log file: " + logFile.toAbsolutePath());
    }

    private static void runModule(String[] args) throws IOException {
        if (args.length < 3) {
            printUsage();
            return;
        }

        ModuleType moduleType = ModuleType.fromString(args[0]);
        Path inputFile = Path.of(args[1]);
        Path outputFile = Path.of(args[2]);
        Path logFile = Path.of(args.length >= 4 ? args[3] : "logs/app.log");

        Logger logger = AppLogger.configure(logFile);
        FunctionFactory factory = new FunctionFactory(EPS, MAX_ITERATIONS, logger);
        CsvFunctionExecutor executor = new CsvFunctionExecutor(new CsvReader(), new CsvWriter(), factory);

        executor.execute(moduleType, inputFile, outputFile);

        System.out.println("Module: " + moduleType);
        System.out.println("Input CSV: " + inputFile.toAbsolutePath());
        System.out.println("Output CSV: " + outputFile.toAbsolutePath());
        System.out.println("Log file: " + logFile.toAbsolutePath());
    }

    private static void printUsage() {
        System.out.println("""
                Usage:
                  1) Run concrete module:
                     gradlew run --args="cos input/cos.csv output/cos-result.csv logs/app.log"

                  2) Run ctg/cot:
                     gradlew run --args="ctg input/ctg.csv output/ctg-result.csv logs/app.log"

                  3) Run whole system:
                     gradlew run --args="system input/system.csv output/system-result.csv logs/app.log"

                  4) Generate sample CSV tables:
                     gradlew run --args="samples samples logs/app.log"

                Available modules:
                  sin, cos, ln, tan, cot, ctg, sec, log2, log3, log5, log10, system
                """);
    }
}
