package org.savadanko.math.functions;

import org.savadanko.math.utils.MathUtils;

public final class SinFunction implements UnaryFunction {
    private final double eps;
    private final int maxIterations;

    public SinFunction(double eps, int maxIterations) {
        this.eps = eps;
        this.maxIterations = maxIterations;
    }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return Double.NaN;
        }

        double normalized = MathUtils.normalizeAngle(x);
        double term = normalized;
        double sum = normalized;

        for (int n = 1; n < maxIterations; n++) {
            term *= -normalized * normalized / ((2.0 * n) * (2.0 * n + 1.0));
            sum += term;

            if (Math.abs(term) < eps) {
                break;
            }
        }

        return sum;
    }
}
