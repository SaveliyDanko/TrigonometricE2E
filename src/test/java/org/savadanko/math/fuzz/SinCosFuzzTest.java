package org.savadanko.math.fuzz;

import com.code_intelligence.jazzer.junit.FuzzTest;
import org.savadanko.math.functions.CosFunction;
import org.savadanko.math.functions.SinFunction;
import org.savadanko.math.utils.MathConstants;

import static org.junit.jupiter.api.Assertions.*;

class SinCosFuzzTest {

    private static final double EPS = 1e-10;
    private static final int MAX_ITERATIONS = 100_000;
    private static final double TOLERANCE = 1e-6;

    private final SinFunction sin = new SinFunction(EPS, MAX_ITERATIONS);
    private final CosFunction cos = new CosFunction(EPS, MAX_ITERATIONS);

    @FuzzTest(maxDuration = "30s")
    void sinNeverThrowsAndStaysInRange(double x) {
        double result = sin.apply(x);

        if (Double.isNaN(x) || Double.isInfinite(x)) {
            assertTrue(Double.isNaN(result),
                    "sin(NaN/Inf) should be NaN, got " + result);
        } else {
            assertFalse(Double.isNaN(result),
                    "sin(" + x + ") should not be NaN");
            assertTrue(result >= -1.0 - TOLERANCE && result <= 1.0 + TOLERANCE,
                    "sin(" + x + ") = " + result + " is out of [-1, 1]");
        }
    }

    @FuzzTest(maxDuration = "30s")
    void cosNeverThrowsAndStaysInRange(double x) {
        double result = cos.apply(x);

        if (Double.isNaN(x) || Double.isInfinite(x)) {
            assertTrue(Double.isNaN(result),
                    "cos(NaN/Inf) should be NaN, got " + result);
        } else {
            assertFalse(Double.isNaN(result),
                    "cos(" + x + ") should not be NaN");
            assertTrue(result >= -1.0 - TOLERANCE && result <= 1.0 + TOLERANCE,
                    "cos(" + x + ") = " + result + " is out of [-1, 1]");
        }
    }

    @FuzzTest(maxDuration = "30s")
    void sinIsOddFunction(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;

        double sinX = sin.apply(x);
        double sinNegX = sin.apply(-x);

        assertEquals(sinX, -sinNegX, TOLERANCE,
                "sin(" + x + ") should equal -sin(" + (-x) + ")");
    }

    @FuzzTest(maxDuration = "30s")
    void cosIsEvenFunction(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;

        double cosX = cos.apply(x);
        double cosNegX = cos.apply(-x);

        assertEquals(cosX, cosNegX, TOLERANCE,
                "cos(" + x + ") should equal cos(" + (-x) + ")");
    }

    @FuzzTest(maxDuration = "30s")
    void pythagoreanIdentity(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;

        double sinX = sin.apply(x);
        double cosX = cos.apply(x);
        double sum = sinX * sinX + cosX * cosX;

        assertEquals(1.0, sum, TOLERANCE,
                "sin^2(" + x + ") + cos^2(" + x + ") = " + sum + ", expected 1.0");
    }

    @FuzzTest(maxDuration = "30s")
    void sinPeriodicity(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e8) return;

        double sinX = sin.apply(x);
        double sinXPlus2Pi = sin.apply(x + MathConstants.TWO_PI);

        assertEquals(sinX, sinXPlus2Pi, TOLERANCE,
                "sin(" + x + ") != sin(" + x + " + 2pi)");
    }

    @FuzzTest(maxDuration = "30s")
    void sinMatchesReference(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e8) return;

        double actual = sin.apply(x);
        double expected = Math.sin(x);

        assertEquals(expected, actual, TOLERANCE,
                "sin(" + x + "): expected " + expected + ", got " + actual);
    }

    @FuzzTest(maxDuration = "30s")
    void cosMatchesReference(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e8) return;

        double actual = cos.apply(x);
        double expected = Math.cos(x);

        assertEquals(expected, actual, TOLERANCE,
                "cos(" + x + "): expected " + expected + ", got " + actual);
    }
}
