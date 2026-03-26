package org.savadanko.math.fuzz;

import com.code_intelligence.jazzer.junit.FuzzTest;
import org.savadanko.math.functions.LnFunction;

import static org.junit.jupiter.api.Assertions.*;

class LnFunctionFuzzTest {

    private static final double EPS = 1e-10;
    private static final int MAX_ITERATIONS = 100_000;
    private static final double TOLERANCE = 1e-6;

    private final LnFunction ln = new LnFunction(EPS, MAX_ITERATIONS);

    @FuzzTest(maxDuration = "30s")
    void lnNeverThrows(double x) {
        double result = ln.apply(x);

        if (Double.isNaN(x) || x < 0) {
            assertTrue(Double.isNaN(result),
                    "ln(" + x + ") should be NaN for negative/NaN input, got " + result);
        } else if (x == 0.0) {
            assertTrue(Double.isInfinite(result) && result < 0,
                    "ln(0) should be -Infinity, got " + result);
        } else if (Double.isInfinite(x) && x > 0) {
            assertTrue(Double.isInfinite(result) && result > 0,
                    "ln(+Inf) should be +Infinity, got " + result);
        }
    }

    @FuzzTest(maxDuration = "30s")
    void lnMonotonicity(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return;
        if (Double.isInfinite(a) || Double.isInfinite(b)) return;
        if (a <= 0 || b <= 0) return;
        if (a == b) return;

        double lnA = ln.apply(a);
        double lnB = ln.apply(b);

        if (a < b) {
            assertTrue(lnA <= lnB + TOLERANCE,
                    "ln should be monotonically increasing: ln(" + a + ")=" + lnA
                            + " > ln(" + b + ")=" + lnB);
        } else {
            assertTrue(lnA >= lnB - TOLERANCE,
                    "ln should be monotonically increasing: ln(" + a + ")=" + lnA
                            + " < ln(" + b + ")=" + lnB);
        }
    }

    @FuzzTest(maxDuration = "30s")
    void lnProductProperty(double a, double b) {
        if (Double.isNaN(a) || Double.isNaN(b)) return;
        if (Double.isInfinite(a) || Double.isInfinite(b)) return;
        if (a <= 0 || b <= 0) return;

        double product = a * b;
        if (Double.isInfinite(product) || product <= 0) return;

        double lnProduct = ln.apply(product);
        double lnSum = ln.apply(a) + ln.apply(b);

        double delta = Math.max(TOLERANCE, Math.abs(lnProduct) * 1e-5);
        assertEquals(lnProduct, lnSum, delta,
                "ln(" + a + " * " + b + ") != ln(" + a + ") + ln(" + b + ")");
    }

    @FuzzTest(maxDuration = "30s")
    void lnMatchesReference(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (x <= 0) return;

        double actual = ln.apply(x);
        double expected = Math.log(x);

        double delta = Math.max(TOLERANCE, Math.abs(expected) * 1e-5);
        assertEquals(expected, actual, delta,
                "ln(" + x + "): expected " + expected + ", got " + actual);
    }

    @FuzzTest(maxDuration = "30s")
    void lnSignProperty(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (x <= 0) return;

        double result = ln.apply(x);

        if (x < 1.0) {
            assertTrue(result < TOLERANCE,
                    "ln(" + x + ") should be negative for x < 1, got " + result);
        } else if (x > 1.0) {
            assertTrue(result > -TOLERANCE,
                    "ln(" + x + ") should be positive for x > 1, got " + result);
        } else {
            assertEquals(0.0, result, TOLERANCE,
                    "ln(1) should be 0, got " + result);
        }
    }
}
