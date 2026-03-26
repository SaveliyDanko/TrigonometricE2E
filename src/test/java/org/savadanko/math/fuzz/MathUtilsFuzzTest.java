package org.savadanko.math.fuzz;

import com.code_intelligence.jazzer.junit.FuzzTest;
import org.savadanko.math.utils.MathConstants;
import org.savadanko.math.utils.MathUtils;

import static org.junit.jupiter.api.Assertions.*;

class MathUtilsFuzzTest {

    private static final double TOLERANCE = 1e-10;

    @FuzzTest(maxDuration = "30s")
    void normalizeAngleAlwaysInRange(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;

        double result = MathUtils.normalizeAngle(x);

        assertTrue(result >= -MathConstants.PI - TOLERANCE
                        && result <= MathConstants.PI + TOLERANCE,
                "normalizeAngle(" + x + ") = " + result + " is out of [-pi, pi]");
    }

    @FuzzTest(maxDuration = "30s")
    void normalizeAnglePreservesTrigValues(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e10) return;

        double normalized = MathUtils.normalizeAngle(x);

        assertEquals(Math.sin(x), Math.sin(normalized), 1e-6,
                "sin should be preserved after normalization for x=" + x);
        assertEquals(Math.cos(x), Math.cos(normalized), 1e-6,
                "cos should be preserved after normalization for x=" + x);
    }

    @FuzzTest(maxDuration = "30s")
    void safeDivNeverThrows(double numerator, double denominator) {
        double result = MathUtils.safeDiv(numerator, denominator, TOLERANCE);

        if (Double.isNaN(numerator) || Double.isNaN(denominator)
                || Math.abs(denominator) <= TOLERANCE) {
            assertTrue(Double.isNaN(result),
                    "safeDiv should return NaN for invalid inputs");
        } else {
            assertFalse(Double.isNaN(result),
                    "safeDiv(" + numerator + ", " + denominator + ") unexpectedly NaN");
        }
    }

    @FuzzTest(maxDuration = "30s")
    void powIntConsistentWithMathPow(long rawBase, int rawExp) {
        double base = (rawBase % 1000) / 10.0; // constrain to reasonable range
        int exponent = Math.abs(rawExp % 20);   // constrain exponent 0..19

        double actual = MathUtils.powInt(base, exponent);
        double expected = Math.pow(base, exponent);

        if (Double.isNaN(expected) || Double.isInfinite(expected)) return;
        if (Double.isNaN(actual) || Double.isInfinite(actual)) return;

        double delta = Math.max(1e-6, Math.abs(expected) * 1e-6);
        assertEquals(expected, actual, delta,
                "powInt(" + base + ", " + exponent + "): expected "
                        + expected + ", got " + actual);
    }

    @FuzzTest(maxDuration = "30s")
    void isZeroSymmetric(double value) {
        boolean result = MathUtils.isZero(value, TOLERANCE);
        boolean resultNeg = MathUtils.isZero(-value, TOLERANCE);

        assertEquals(result, resultNeg,
                "isZero should be symmetric: isZero(" + value + ") != isZero(" + (-value) + ")");
    }
}
