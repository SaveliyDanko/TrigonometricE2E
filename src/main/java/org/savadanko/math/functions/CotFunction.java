package org.savadanko.math.functions;

import org.savadanko.math.utils.MathUtils;

public final class CotFunction implements UnaryFunction {
    private final UnaryFunction tan;
    private final double eps;

    public CotFunction(UnaryFunction tan, double eps) {
        this.tan = tan;
        this.eps = eps;
    }

    @Override
    public double apply(double x) {
        return MathUtils.safeDiv(1.0, tan.apply(x), eps);
    }
}
