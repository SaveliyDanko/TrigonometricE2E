package org.savadanko.math.fuzz;

import com.code_intelligence.jazzer.junit.FuzzTest;
import org.savadanko.math.functions.LnFunction;
import org.savadanko.math.functions.LogFunction;

import static org.junit.jupiter.api.Assertions.*;

class LogFunctionFuzzTest {

    private static final double EPS = 1e-10;
    private static final int MAX_ITERATIONS = 100_000;
    private static final double TOLERANCE = 1e-6;

    private final LnFunction ln = new LnFunction(EPS, MAX_ITERATIONS);
    private final LogFunction log2 = new LogFunction(ln, 2.0, EPS);
    private final LogFunction log3 = new LogFunction(ln, 3.0, EPS);
    private final LogFunction log10 = new LogFunction(ln, 10.0, EPS);

    @FuzzTest(maxDuration = "30s")
    void log2MatchesReference(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (x <= 0) return;

        double actual = log2.apply(x);
        double expected = Math.log(x) / Math.log(2.0);

        double delta = Math.max(TOLERANCE, Math.abs(expected) * 1e-5);
        assertEquals(expected, actual, delta,
                "log2(" + x + "): expected " + expected + ", got " + actual);
    }

    @FuzzTest(maxDuration = "30s")
    void log10MatchesReference(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (x <= 0) return;

        double actual = log10.apply(x);
        double expected = Math.log10(x);

        double delta = Math.max(TOLERANCE, Math.abs(expected) * 1e-5);
        assertEquals(expected, actual, delta,
                "log10(" + x + "): expected " + expected + ", got " + actual);
    }

    @FuzzTest(maxDuration = "30s")
    void changeOfBaseConsistency(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (x <= 0) return;

        double log2X = log2.apply(x);
        double log3X = log3.apply(x);

        if (Double.isNaN(log2X) || Double.isNaN(log3X)) return;
        if (Double.isInfinite(log2X) || Double.isInfinite(log3X)) return;
        if (Math.abs(log3X) < EPS) return;

        double ratio = log2X / log3X;
        double expectedRatio = Math.log(3.0) / Math.log(2.0);

        assertEquals(expectedRatio, ratio, TOLERANCE * 10,
                "log2/log3 ratio should be constant: expected "
                        + expectedRatio + ", got " + ratio + " for x=" + x);
    }

    @FuzzTest(maxDuration = "30s")
    void logNegativeReturnsNaN(double x) {
        if (x >= 0 || Double.isNaN(x)) return;

        double result = log2.apply(x);
        assertTrue(Double.isNaN(result),
                "log2(" + x + ") should be NaN for negative input, got " + result);
    }

    @FuzzTest(maxDuration = "30s")
    void logMonotonicity(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return;
        if (Double.isInfinite(a) || Double.isInfinite(b)) return;
        if (a <= 0 || b <= 0 || a == b) return;

        double logA = log2.apply(a);
        double logB = log2.apply(b);

        if (a < b) {
            assertTrue(logA <= logB + TOLERANCE,
                    "log2 should be monotonically increasing");
        } else {
            assertTrue(logA >= logB - TOLERANCE,
                    "log2 should be monotonically increasing");
        }
    }
}
