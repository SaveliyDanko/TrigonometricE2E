package org.savadanko.math.functions;

import org.savadanko.math.utils.MathUtils;

public final class TanFunction implements UnaryFunction {
    private final UnaryFunction sin;
    private final UnaryFunction cos;
    private final double eps;

    public TanFunction(UnaryFunction sin, UnaryFunction cos, double eps) {
        this.sin = sin;
        this.cos = cos;
        this.eps = eps;
    }

    @Override
    public double apply(double x) {
        double sinX = sin.apply(x);
        double cosX = cos.apply(x);
        return MathUtils.safeDiv(sinX, cosX, eps);
    }
}
