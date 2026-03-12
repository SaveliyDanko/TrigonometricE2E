package org.savadanko.math.functions;

import lombok.AllArgsConstructor;
import org.savadanko.math.utils.MathUtils;

@AllArgsConstructor
public final class SystemFunction implements UnaryFunction {
    private final UnaryFunction cot;
    private final UnaryFunction tan;
    private final UnaryFunction sec;

    private final UnaryFunction log2;
    private final UnaryFunction log3;
    private final UnaryFunction log5;
    private final UnaryFunction log10;

    private final double eps;

    @Override
    public double apply(double x) {
        return x <= 0 ? calculateTrigBranch(x) : calculateLogBranch(x);
    }

    private double calculateTrigBranch(double x) {
        double cotX = cot.apply(x);
        double tanX = tan.apply(x);
        double secX = sec.apply(x);

        if (Double.isNaN(cotX) || Double.isNaN(tanX) || Double.isNaN(secX)) {
            return Double.NaN;
        }

        double firstFactor = MathUtils.safeDiv(cotX * cotX, tanX, eps);
        if (Double.isNaN(firstFactor)) {
            return Double.NaN;
        }

        double core = (firstFactor - tanX) * ((cotX - secX) * tanX);
        return MathUtils.powInt(MathUtils.powInt(core, 3), 2);
    }

    private double calculateLogBranch(double x) {
        double log2X = log2.apply(x);
        double log3X = log3.apply(x);
        double log5X = log5.apply(x);
        double log10X = log10.apply(x);

        if (Double.isNaN(log2X) || Double.isNaN(log3X) || Double.isNaN(log5X) || Double.isNaN(log10X)) {
            return Double.NaN;
        }

        double log2Ratio = MathUtils.safeDiv(log2X, log2X, eps);

        double left = MathUtils.safeDiv(MathUtils.powInt(log10X, 3), log5X, eps);
        left *= log3X;
        left = MathUtils.safeDiv(left, log2Ratio, eps);

        double right = ((log2X - log2X) * log2X)
                + MathUtils.safeDiv(log3X, log10X, eps);

        return left * right;
    }
}
