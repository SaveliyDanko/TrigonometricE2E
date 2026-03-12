package org.savadanko.math.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public final class AppLogger {
    private AppLogger() {
    }

    public static Logger configure(Path logFile) throws IOException {
        if (logFile.getParent() != null) {
            Files.createDirectories(logFile.getParent());
        }

        Logger logger = Logger.getLogger("com.example.math-app");
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);

        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
            handler.close();
        }

        FileHandler fileHandler = new FileHandler(logFile.toString(), true);
        fileHandler.setLevel(Level.INFO);
        fileHandler.setEncoding("UTF-8");
        fileHandler.setFormatter(new Formatter() {
            private final DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            .withZone(ZoneId.systemDefault());

            @Override
            public String format(LogRecord record) {
                return "%s | %s | %s%n".formatted(
                        formatter.format(Instant.ofEpochMilli(record.getMillis())),
                        record.getLevel().getName(),
                        record.getMessage()
                );
            }
        });

        logger.addHandler(fileHandler);
        return logger;
    }
}
