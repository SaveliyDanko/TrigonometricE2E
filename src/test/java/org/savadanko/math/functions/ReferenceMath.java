package org.savadanko.math.functions;

final class ReferenceMath {
    private ReferenceMath() {
    }

    static double system(double x) {
        return x <= 0 ? trigBranch(x) : logBranch(x);
    }

    static double trigBranch(double x) {
        double cot = 1.0 / Math.tan(x);
        double tan = Math.tan(x);
        double sec = 1.0 / Math.cos(x);

        double core = ((((cot * cot) / tan) - tan) * ((cot - sec) * tan));
        return Math.pow(Math.pow(core, 3.0), 2.0);
    }

    static double logBranch(double x) {
        double log2 = Math.log(x) / Math.log(2.0);
        double log3 = Math.log(x) / Math.log(3.0);
        double log5 = Math.log(x) / Math.log(5.0);
        double log10 = Math.log10(x);

        return (((((Math.pow(log10, 3)) / log5) * log3) / (log2 / log2))
                * (((log2 - log2) * log2) + (log3 / log10)));
    }
}