package org.savadanko.math.functions;

import org.savadanko.math.utils.MathConstants;

public final class LnFunction implements UnaryFunction {
    private final double eps;
    private final int maxIterations;

    public LnFunction(double eps, int maxIterations) {
        this.eps = eps;
        this.maxIterations = maxIterations;
    }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || x < 0.0) {
            return Double.NaN;
        }
        if (x == 0.0) {
            return Double.NEGATIVE_INFINITY;
        }
        if (Double.isInfinite(x)) {
            return Double.POSITIVE_INFINITY;
        }
        if (x == 1.0) {
            return 0.0;
        }

        int powerOfTwo = Math.getExponent(x);
        double mantissa = Math.scalb(x, -powerOfTwo); // x = mantissa * 2^powerOfTwo

        // Подтягиваем mantissa ближе к 1 для более быстрой сходимости
        if (mantissa > 1.5) {
            mantissa /= 2.0;
            powerOfTwo++;
        }

        // ln(m) = 2 * (z + z^3/3 + z^5/5 + ...), z = (m - 1)/(m + 1)
        double z = (mantissa - 1.0) / (mantissa + 1.0);
        double zSquared = z * z;

        double term = z;
        double sum = 0.0;
        int denominator = 1;

        for (int i = 0; i < maxIterations; i++) {
            double current = term / denominator;
            sum += current;

            if (Math.abs(current) < eps) {
                break;
            }

            term *= zSquared;
            denominator += 2;
        }

        return 2.0 * sum + powerOfTwo * MathConstants.LN_2;
    }
}