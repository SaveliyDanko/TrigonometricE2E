package org.savadanko.math.utils;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class MathUtils {
    public static double normalizeAngle(double x) {
        double result = x % MathConstants.TWO_PI;
        if (result > MathConstants.PI) {
            result -= MathConstants.TWO_PI;
        } else if (result < -MathConstants.PI) {
            result += MathConstants.TWO_PI;
        }
        return result;
    }

    public static boolean isZero(double value, double eps) {
        return Math.abs(value) <= eps;
    }

    public static double safeDiv(double numerator, double denominator, double eps) {
        if (Double.isNaN(numerator) || Double.isNaN(denominator) || isZero(denominator, eps)) {
            return Double.NaN;
        }
        return numerator / denominator;
    }

    public static double powInt(double value, int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Negative exponent is not supported");
        }

        double result = 1.0;
        for (int i = 0; i < exponent; i++) {
            result *= value;
        }
        return result;
    }
}
