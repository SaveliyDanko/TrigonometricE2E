package org.savadanko.math.functions;

import org.savadanko.math.utils.MathUtils;

public final class LogFunction implements UnaryFunction {
    private final UnaryFunction ln;
    private final double baseLn;
    private final double eps;

    public LogFunction(UnaryFunction ln, double base, double eps) {
        this.ln = ln;
        this.baseLn = ln.apply(base);
        this.eps = eps;

        if (Double.isNaN(baseLn) || MathUtils.isZero(baseLn, eps)) {
            throw new IllegalArgumentException("Invalid logarithm base: " + base);
        }
    }

    @Override
    public double apply(double x) {
        return MathUtils.safeDiv(ln.apply(x), baseLn, eps);
    }
}
