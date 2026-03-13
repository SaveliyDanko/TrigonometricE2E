package org.savadanko.math.utils;

import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


@NoArgsConstructor
public final class TabularValidator {

    private static final double VALIDATION_EPS = 1e-10;

    private static final double PI_6 = MathConstants.PI / 6.0;   // 30°
    private static final double PI_4 = MathConstants.PI / 4.0;   // 45°
    private static final double PI_3 = MathConstants.PI / 3.0;   // 60°
    private static final double PI_2 = MathConstants.PI / 2.0;   // 90°

    private static final Map<Double, Double> SIN_TABLE = new HashMap<>() {{
        put(0.0, 0.0);
        put(PI_6, 0.5);                    // sin(30°) = 1/2
        put(PI_4, Math.sqrt(2) / 2.0);     // sin(45°) = √2/2
        put(PI_3, Math.sqrt(3) / 2.0);     // sin(60°) = √3/2
        put(PI_2, 1.0);                    // sin(90°) = 1
        put(MathConstants.PI, 0.0);        // sin(180°) = 0
        put(-PI_6, -0.5);                  // sin(-30°) = -1/2
        put(-PI_4, -Math.sqrt(2) / 2.0);   // sin(-45°) = -√2/2
        put(-PI_3, -Math.sqrt(3) / 2.0);   // sin(-60°) = -√3/2
        put(-PI_2, -1.0);                  // sin(-90°) = -1
        put(-MathConstants.PI, 0.0);       // sin(-180°) = 0
    }};

    private static final Map<Double, Double> COS_TABLE = new HashMap<>() {{
        put(0.0, 1.0);
        put(PI_6, Math.sqrt(3) / 2.0);     // cos(30°) = √3/2
        put(PI_4, Math.sqrt(2) / 2.0);     // cos(45°) = √2/2
        put(PI_3, 0.5);                    // cos(60°) = 1/2
        put(PI_2, 0.0);                    // cos(90°) = 0
        put(MathConstants.PI, -1.0);       // cos(180°) = -1
        put(-PI_6, Math.sqrt(3) / 2.0);    // cos(-30°) = √3/2
        put(-PI_4, Math.sqrt(2) / 2.0);    // cos(-45°) = √2/2
        put(-PI_3, 0.5);                   // cos(-60°) = 1/2
        put(-PI_2, 0.0);                   // cos(-90°) = 0
        put(-MathConstants.PI, -1.0);      // cos(-180°) = -1
    }};

    private static final Map<Double, Double> TAN_TABLE = new HashMap<>() {{
        put(0.0, 0.0);
        put(PI_6, 1.0 / Math.sqrt(3));     // tan(30°) = 1/√3
        put(PI_4, 1.0);                    // tan(45°) = 1
        put(PI_3, Math.sqrt(3));           // tan(60°) = √3
        put(MathConstants.PI, 0.0);        // tan(180°) = 0
        put(-PI_6, -1.0 / Math.sqrt(3));   // tan(-30°) = -1/√3
        put(-PI_4, -1.0);                  // tan(-45°) = -1
        put(-PI_3, -Math.sqrt(3));         // tan(-60°) = -√3
        put(-MathConstants.PI, 0.0);       // tan(-180°) = 0
    }};

    private static final Map<Double, Double> LN_TABLE = new HashMap<>() {{
        put(1.0, 0.0);                     // ln(1) = 0
        put(Math.E, 1.0);                  // ln(e) = 1
        put(2.0, MathConstants.LN_2);      // ln(2)
        put(0.5, -MathConstants.LN_2);     // ln(1/2) = -ln(2)
        put(Math.E * Math.E, 2.0);        // ln(e²) = 2
        put(1.0 / Math.E, -1.0);          // ln(1/e) = -1
    }};


    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;
        private final double expectedValue;
        private final double actualValue;
        private final double delta;

        public ValidationResult(boolean isValid, String errorMessage,
                              double expectedValue, double actualValue, double delta) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
            this.expectedValue = expectedValue;
            this.actualValue = actualValue;
            this.delta = delta;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public double getExpectedValue() {
            return expectedValue;
        }

        public double getActualValue() {
            return actualValue;
        }

        public double getDelta() {
            return delta;
        }

        @Override
        public String toString() {
            if (isValid) {
                return "ValidationResult{valid, delta=" + delta + "}";
            } else {
                return "ValidationResult{invalid, error='" + errorMessage +
                       "', expected=" + expectedValue + ", actual=" + actualValue +
                       ", delta=" + delta + "}";
            }
        }
    }

    
    public static ValidationResult validateValue(ModuleType moduleType, double x, double y) {
        Map<Double, Double> table = getTableForModule(moduleType);

        if (table == null) {
            return new ValidationResult(true, "No tabular data for module " + moduleType,
                                      Double.NaN, y, Double.NaN);
        }

        double normalizedX = isTrigFunction(moduleType) ? MathUtils.normalizeAngle(x) : x;

        Double expectedValue = null;
        for (Map.Entry<Double, Double> entry : table.entrySet()) {
            if (Math.abs(entry.getKey() - normalizedX) < VALIDATION_EPS) {
                expectedValue = entry.getValue();
                break;
            }
        }

        if (expectedValue == null) {
            return new ValidationResult(true, "No tabular data for x=" + x,
                                      Double.NaN, y, Double.NaN);
        }

        double delta = Math.abs(y - expectedValue);
        boolean isValid = delta < VALIDATION_EPS;

        if (isValid) {
            return new ValidationResult(true, null, expectedValue, y, delta);
        } else {
            String error = String.format("Function %s at x=%.6f: expected=%.10f, actual=%.10f, delta=%.2e",
                                        moduleType, x, expectedValue, y, delta);
            return new ValidationResult(false, error, expectedValue, y, delta);
        }
    }

   
    public static List<ValidationResult> validateValues(ModuleType moduleType,
                                                       List<Double> xValues,
                                                       List<Double> yValues) {
        if (xValues.size() != yValues.size()) {
            throw new IllegalArgumentException("x and y lists must have the same size");
        }

        List<ValidationResult> results = new ArrayList<>();

        for (int i = 0; i < xValues.size(); i++) {
            results.add(validateValue(moduleType, xValues.get(i), yValues.get(i)));
        }

        return results;
    }

    
    public static int getTabularValuesCount(ModuleType moduleType) {
        Map<Double, Double> table = getTableForModule(moduleType);
        return table != null ? table.size() : 0;
    }

    
    public static List<Double> getTabularXValues(ModuleType moduleType) {
        Map<Double, Double> table = getTableForModule(moduleType);
        return table != null ? new ArrayList<>(table.keySet()) : new ArrayList<>();
    }

  
    public static boolean hasTabularValue(ModuleType moduleType, double x) {
        Map<Double, Double> table = getTableForModule(moduleType);
        if (table == null) return false;

        double normalizedX = isTrigFunction(moduleType) ? MathUtils.normalizeAngle(x) : x;

        return table.keySet().stream()
                .anyMatch(key -> Math.abs(key - normalizedX) < VALIDATION_EPS);
    }

    private static Map<Double, Double> getTableForModule(ModuleType moduleType) {
        return switch (moduleType) {
            case SIN -> SIN_TABLE;
            case COS -> COS_TABLE;
            case TAN -> TAN_TABLE;
            case LN -> LN_TABLE;
            default -> null; 
        };
    }

    private static boolean isTrigFunction(ModuleType moduleType) {
        return moduleType == ModuleType.SIN ||
               moduleType == ModuleType.COS ||
               moduleType == ModuleType.TAN ||
               moduleType == ModuleType.COT ||
               moduleType == ModuleType.SEC;
    }
}