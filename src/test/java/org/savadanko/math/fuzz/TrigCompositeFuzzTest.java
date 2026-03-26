package org.savadanko.math.fuzz;

import com.code_intelligence.jazzer.junit.FuzzTest;
import org.savadanko.math.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class TrigCompositeFuzzTest {

    private static final double EPS = 1e-10;
    private static final int MAX_ITERATIONS = 100_000;
    private static final double TOLERANCE = 1e-6;

    private final SinFunction sin = new SinFunction(EPS, MAX_ITERATIONS);
    private final CosFunction cos = new CosFunction(EPS, MAX_ITERATIONS);
    private final TanFunction tan = new TanFunction(sin, cos, EPS);
    private final CotFunction cot = new CotFunction(tan, EPS);
    private final SecFunction sec = new SecFunction(cos, EPS);

    @FuzzTest(maxDuration = "30s")
    void tanEqualssinOverCos(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e8) return;

        double sinX = sin.apply(x);
        double cosX = cos.apply(x);
        double tanX = tan.apply(x);

        if (Math.abs(cosX) < EPS) {
            assertTrue(Double.isNaN(tanX),
                    "tan should be NaN at singularity, got " + tanX);
            return;
        }

        double expected = sinX / cosX;
        assertEquals(expected, tanX, TOLERANCE,
                "tan(" + x + ") != sin(" + x + ")/cos(" + x + ")");
    }

    @FuzzTest(maxDuration = "30s")
    void cotEqualsOneOverTan(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e8) return;

        double tanX = tan.apply(x);
        double cotX = cot.apply(x);

        if (Double.isNaN(tanX) || Math.abs(tanX) < EPS) {
            assertTrue(Double.isNaN(cotX),
                    "cot should be NaN when tan is 0 or NaN");
            return;
        }

        double expected = 1.0 / tanX;
        double delta = Math.max(TOLERANCE, Math.abs(expected) * 1e-5);
        assertEquals(expected, cotX, delta,
                "cot(" + x + ") != 1/tan(" + x + ")");
    }

    @FuzzTest(maxDuration = "30s")
    void secEqualsOneOverCos(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e8) return;

        double cosX = cos.apply(x);
        double secX = sec.apply(x);

        if (Math.abs(cosX) < EPS) {
            assertTrue(Double.isNaN(secX),
                    "sec should be NaN at singularity");
            return;
        }

        double expected = 1.0 / cosX;
        double delta = Math.max(TOLERANCE, Math.abs(expected) * 1e-5);
        assertEquals(expected, secX, delta,
                "sec(" + x + ") != 1/cos(" + x + ")");
    }

    @FuzzTest(maxDuration = "30s")
    void tanMatchesReference(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e8) return;

        double cosX = cos.apply(x);
        if (Math.abs(cosX) < EPS) return;

        double actual = tan.apply(x);
        double expected = Math.tan(x);

        if (Double.isNaN(expected)) {
            assertTrue(Double.isNaN(actual));
            return;
        }

        double delta = Math.max(TOLERANCE, Math.abs(expected) * 1e-4);
        assertEquals(expected, actual, delta,
                "tan(" + x + "): expected " + expected + ", got " + actual);
    }

    @FuzzTest(maxDuration = "30s")
    void tanSquaredPlusOneEqualsSecSquared(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) return;
        if (Math.abs(x) > 1e8) return;

        double cosX = cos.apply(x);
        if (Math.abs(cosX) < EPS) return;

        double tanX = tan.apply(x);
        double secX = sec.apply(x);

        if (Double.isNaN(tanX) || Double.isNaN(secX)) return;
        if (Math.abs(tanX) > 1e6 || Math.abs(secX) > 1e6) return;

        double lhs = tanX * tanX + 1.0;
        double rhs = secX * secX;

        double delta = Math.max(TOLERANCE, Math.abs(rhs) * 1e-4);
        assertEquals(lhs, rhs, delta,
                "tan^2(" + x + ") + 1 != sec^2(" + x + ")");
    }
}
