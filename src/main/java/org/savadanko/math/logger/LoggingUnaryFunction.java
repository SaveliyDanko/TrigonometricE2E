package org.savadanko.math.logger;

import org.savadanko.math.functions.UnaryFunction;

import java.util.logging.Logger;

public final class LoggingUnaryFunction implements UnaryFunction {
    private final String moduleName;
    private final UnaryFunction delegate;
    private final Logger logger;

    public LoggingUnaryFunction(String moduleName, UnaryFunction delegate, Logger logger) {
        this.moduleName = moduleName;
        this.delegate = delegate;
        this.logger = logger;
    }

    @Override
    public double apply(double x) {
        try {
            double result = delegate.apply(x);
            logger.info(() -> "module=%s; input=%s; result=%s"
                    .formatted(moduleName, format(x), format(result)));
            return result;
        } catch (RuntimeException e) {
            logger.severe(() -> "module=%s; input=%s; error=%s"
                    .formatted(moduleName, format(x), e.getMessage()));
            throw e;
        }
    }

    private String format(double value) {
        if (Double.isNaN(value)) {
            return "NaN";
        }
        if (Double.isInfinite(value)) {
            return value > 0 ? "Infinity" : "-Infinity";
        }
        return Double.toString(value);
    }
}