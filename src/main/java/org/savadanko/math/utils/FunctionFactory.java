package org.savadanko.math.utils;

import org.savadanko.math.functions.*;
import org.savadanko.math.logger.LoggingUnaryFunction;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

public final class FunctionFactory {
    private final double eps;
    private final int maxIterations;
    private final Logger logger;

    public FunctionFactory(double eps, int maxIterations, Logger logger) {
        this.eps = eps;
        this.maxIterations = maxIterations;
        this.logger = logger;
    }

    public UnaryFunction create(ModuleType moduleType) {
        Map<ModuleType, UnaryFunction> functions = buildFunctions();
        return functions.get(moduleType);
    }

    private Map<ModuleType, UnaryFunction> buildFunctions() {
        Map<ModuleType, UnaryFunction> functions = new EnumMap<>(ModuleType.class);

        UnaryFunction sin = new LoggingUnaryFunction(
                "sin",
                new SinFunction(eps, maxIterations),
                logger
        );

        UnaryFunction cos = new LoggingUnaryFunction(
                "cos",
                new CosFunction(eps, maxIterations),
                logger
        );

        UnaryFunction ln = new LoggingUnaryFunction(
                "ln",
                new LnFunction(eps, maxIterations),
                logger
        );

        UnaryFunction tan = new LoggingUnaryFunction(
                "tan",
                new TanFunction(sin, cos, eps),
                logger
        );

        UnaryFunction cot = new LoggingUnaryFunction(
                "cot",
                new CotFunction(tan, eps),
                logger
        );

        UnaryFunction sec = new LoggingUnaryFunction(
                "sec",
                new SecFunction(cos, eps),
                logger
        );

        UnaryFunction log2 = new LoggingUnaryFunction(
                "log_2",
                new LogFunction(ln, 2.0, eps),
                logger
        );

        UnaryFunction log3 = new LoggingUnaryFunction(
                "log_3",
                new LogFunction(ln, 3.0, eps),
                logger
        );

        UnaryFunction log5 = new LoggingUnaryFunction(
                "log_5",
                new LogFunction(ln, 5.0, eps),
                logger
        );

        UnaryFunction log10 = new LoggingUnaryFunction(
                "log_10",
                new LogFunction(ln, 10.0, eps),
                logger
        );

        UnaryFunction system = new LoggingUnaryFunction(
                "system",
                new SystemFunction(cot, tan, sec, log2, log3, log5, log10, eps),
                logger
        );

        functions.put(ModuleType.SIN, sin);
        functions.put(ModuleType.COS, cos);
        functions.put(ModuleType.LN, ln);
        functions.put(ModuleType.TAN, tan);
        functions.put(ModuleType.COT, cot);
        functions.put(ModuleType.SEC, sec);
        functions.put(ModuleType.LOG2, log2);
        functions.put(ModuleType.LOG3, log3);
        functions.put(ModuleType.LOG5, log5);
        functions.put(ModuleType.LOG10, log10);
        functions.put(ModuleType.SYSTEM, system);

        return functions;
    }
}
