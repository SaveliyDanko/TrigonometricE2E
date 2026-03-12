package org.savadanko.math.functions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class IntegrationTestSupport {
    protected static final double EPS = 1e-10;
    protected static final int MAX_ITERATIONS = 100_000;

    protected static void assertClose(double expected, double actual) {
        if (Double.isNaN(expected)) {
            assertTrue(Double.isNaN(actual));
            return;
        }

        double delta = Math.max(1e-6, Math.abs(expected) * 1e-5);
        assertEquals(expected, actual, delta);
    }

    protected static SystemFunction createRealSystem() {
        UnaryFunction sin = new SinFunction(EPS, MAX_ITERATIONS);
        UnaryFunction cos = new CosFunction(EPS, MAX_ITERATIONS);
        UnaryFunction ln = new LnFunction(EPS, MAX_ITERATIONS);

        UnaryFunction tan = new TanFunction(sin, cos, EPS);
        UnaryFunction cot = new CotFunction(tan, EPS);
        UnaryFunction sec = new SecFunction(cos, EPS);

        UnaryFunction log2 = new LogFunction(ln, 2.0, EPS);
        UnaryFunction log3 = new LogFunction(ln, 3.0, EPS);
        UnaryFunction log5 = new LogFunction(ln, 5.0, EPS);
        UnaryFunction log10 = new LogFunction(ln, 10.0, EPS);

        return new SystemFunction(cot, tan, sec, log2, log3, log5, log10, EPS);
    }
}
