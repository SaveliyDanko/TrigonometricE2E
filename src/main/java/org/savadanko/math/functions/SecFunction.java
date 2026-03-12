package org.savadanko.math.functions;

import org.savadanko.math.utils.MathUtils;

public final class SecFunction implements UnaryFunction {
    private final UnaryFunction cos;
    private final double eps;

    public SecFunction(UnaryFunction cos, double eps) {
        this.cos = cos;
        this.eps = eps;
    }

    @Override
    public double apply(double x) {
        return MathUtils.safeDiv(1.0, cos.apply(x), eps);
    }
}
