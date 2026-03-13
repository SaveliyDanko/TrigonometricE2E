package org.savadanko.math;

import org.savadanko.math.csv.CsvFunctionExecutor;
import org.savadanko.math.csv.CsvGenerationConfig;
import org.savadanko.math.csv.CsvGenerator;
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

        if ("generate".equalsIgnoreCase(args[0])) {
            runCsvGeneration(args);
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

    private static void runCsvGeneration(String[] args) throws IOException {
        if (args.length < 6) {
            System.err.println("Error: Not enough arguments for CSV generation");
            printUsage();
            return;
        }

        try {
            ModuleType moduleType = ModuleType.fromString(args[1]);
            double startX = Double.parseDouble(args[2]);
            double endX = Double.parseDouble(args[3]);
            double step = Double.parseDouble(args[4]);
            Path outputFile = Path.of(args[5]);
            String separator = args.length >= 7 ? args[6] : ",";
            Path logFile = Path.of(args.length >= 8 ? args[7] : "logs/app.log");

            Logger logger = AppLogger.configure(logFile);
            CsvGenerator generator = new CsvGenerator(EPS, MAX_ITERATIONS, logger);

            CsvGenerationConfig config = new CsvGenerationConfig(
                    moduleType,
                    startX,
                    endX,
                    step,
                    separator
            );

            generator.generateCsv(config, outputFile);

            System.out.println("CSV generation completed successfully!");
            System.out.println("Module: " + moduleType);
            System.out.println("Range: [" + startX + ", " + endX + "] with step " + step);
            System.out.println("Output CSV: " + outputFile.toAbsolutePath());
            System.out.println("Separator: '" + separator + "'");
            System.out.println("Log file: " + logFile.toAbsolutePath());

        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid number format in arguments: " + e.getMessage());
            printUsage();
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            printUsage();
        }
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

                  5) Generate CSV with custom range and step:
                     gradlew run --args="generate MODULE START_X END_X STEP OUTPUT_FILE [SEPARATOR] [LOG_FILE]"
                     Examples:
                       gradlew run --args="generate cos -3.14 3.14 0.1 output/cos-generated.csv"
                       gradlew run --args="generate sin 0 6.28 0.5 output/sin-generated.csv ; logs/app.log"
                       gradlew run --args="generate system -2 10 0.25 output/system-generated.csv , logs/app.log"

                Available modules:
                  sin, cos, ln, tan, cot, ctg, sec, log2, log3, log5, log10, system
                """);
    }
}
