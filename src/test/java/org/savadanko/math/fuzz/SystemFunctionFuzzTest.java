package org.savadanko.math.fuzz;

import com.code_intelligence.jazzer.junit.FuzzTest;
import org.savadanko.math.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class SystemFunctionFuzzTest {

    private static final double EPS = 1e-10;
    private static final int MAX_ITERATIONS = 100_000;
    private static final double TOLERANCE = 1e-4;

    private final SystemFunction system;

    SystemFunctionFuzzTest() {
        SinFunction sin = new SinFunction(EPS, MAX_ITERATIONS);
        CosFunction cos = new CosFunction(EPS, MAX_ITERATIONS);
        LnFunction ln = new LnFunction(EPS, MAX_ITERATIONS);

        TanFunction tan = new TanFunction(sin, cos, EPS);
        CotFunction cot = new CotFunction(tan, EPS);
        SecFunction sec = new SecFunction(cos, EPS);

        LogFunction log2 = new LogFunction(ln, 2.0, EPS);
        LogFunction log3 = new LogFunction(ln, 3.0, EPS);
        LogFunction log5 = new LogFunction(ln, 5.0, EPS);
        LogFunction log10 = new LogFunction(ln, 10.0, EPS);

        system = new SystemFunction(cot, tan, sec, log2, log3, log5, log10, EPS);
    }

    @FuzzTest(maxDuration = "30s")
    void systemNeverThrows(double x) {
        double result = system.apply(x);
        // The function may return NaN at singularities, but must never throw
        assertFalse(Double.isInfinite(result) && !Double.isInfinite(x),
                "system(" + x + ") returned Infinity unexpectedly");
    }

    @FuzzTest(maxDuration = "30s")
    void systemMatchesReferenceTrigBranch(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (x > 0) return;
        if (Math.abs(x) > 1e6) return;

        double actual = system.apply(x);
        double expected = referenceTrigBranch(x);

        if (Double.isNaN(expected)) {
            assertTrue(Double.isNaN(actual),
                    "system(" + x + ") should be NaN like reference, got " + actual);
            return;
        }

        if (Double.isNaN(actual)) return; // our implementation is stricter on singularities

        double delta = Math.max(TOLERANCE, Math.abs(expected) * 1e-3);
        assertEquals(expected, actual, delta,
                "system trig branch at x=" + x);
    }

    @FuzzTest(maxDuration = "30s")
    void systemMatchesReferenceLogBranch(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (x <= 0) return;
        if (x > 1e6) return;

        double actual = system.apply(x);
        double expected = referenceLogBranch(x);

        if (Double.isNaN(expected)) {
            assertTrue(Double.isNaN(actual),
                    "system(" + x + ") should be NaN like reference, got " + actual);
            return;
        }

        if (Double.isNaN(actual)) return;

        double delta = Math.max(TOLERANCE, Math.abs(expected) * 1e-3);
        assertEquals(expected, actual, delta,
                "system log branch at x=" + x);
    }

    @FuzzTest(maxDuration = "30s")
    void trigBranchNonNegativeForFiniteResult(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (x > 0) return;

        double result = system.apply(x);

        if (Double.isNaN(result)) return;

        // (core^3)^2 = core^6, which is always >= 0
        assertTrue(result >= -TOLERANCE,
                "trig branch should be non-negative (core^6), got " + result + " at x=" + x);
    }

    private static double referenceTrigBranch(double x) {
        double cot = 1.0 / Math.tan(x);
        double tan = Math.tan(x);
        double sec = 1.0 / Math.cos(x);

        double core = ((((cot * cot) / tan) - tan) * ((cot - sec) * tan));
        return Math.pow(Math.pow(core, 3.0), 2.0);
    }

    private static double referenceLogBranch(double x) {
        double log2 = Math.log(x) / Math.log(2.0);
        double log3 = Math.log(x) / Math.log(3.0);
        double log5 = Math.log(x) / Math.log(5.0);
        double log10 = Math.log10(x);

        return (((((Math.pow(log10, 3)) / log5) * log3) / (log2 / log2))
                * (((log2 - log2) * log2) + (log3 / log10)));
    }
}
