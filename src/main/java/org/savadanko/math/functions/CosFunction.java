package org.savadanko.math.functions;

import org.savadanko.math.utils.MathUtils;

public final class CosFunction implements UnaryFunction {
    private final double eps;
    private final int maxIterations;

    public CosFunction(double eps, int maxIterations) {
        this.eps = eps;
        this.maxIterations = maxIterations;
    }

    @Override
    public double apply(double x) {
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            return Double.NaN;
        }

        double normalized = MathUtils.normalizeAngle(x);
        double term = 1.0;
        double sum = 1.0;

        for (int n = 1; n < maxIterations; n++) {
            term *= -normalized * normalized / ((2.0 * n - 1.0) * (2.0 * n));
            sum += term;

            if (Math.abs(term) < eps) {
                break;
            }
        }

        return sum;
    }
}
