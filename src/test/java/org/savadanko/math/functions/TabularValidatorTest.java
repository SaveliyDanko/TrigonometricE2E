package org.savadanko.math.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.savadanko.math.utils.ModuleType;
import org.savadanko.math.utils.TabularValidator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TabularValidatorTest extends IntegrationTestSupport {

    @Test
    void shouldHaveTabularDataForSupportedModules() {
        assertTrue(TabularValidator.getTabularValuesCount(ModuleType.SIN) > 0);
        assertTrue(TabularValidator.getTabularValuesCount(ModuleType.COS) > 0);
        assertTrue(TabularValidator.getTabularValuesCount(ModuleType.TAN) > 0);
        assertTrue(TabularValidator.getTabularValuesCount(ModuleType.LN) > 0);
    }

    @Test
    void shouldReturnZeroForUnsupportedModules() {
        assertEquals(0, TabularValidator.getTabularValuesCount(ModuleType.COT));
        assertEquals(0, TabularValidator.getTabularValuesCount(ModuleType.SEC));
        assertEquals(0, TabularValidator.getTabularValuesCount(ModuleType.LOG2));
    }

    @ParameterizedTest
    @MethodSource("sinTabularValues")
    void shouldValidateSinValues(TabularTestCase testCase) {
        TabularValidator.ValidationResult result =
            TabularValidator.validateValue(ModuleType.SIN, testCase.x, testCase.expectedY);

        assertTrue(result.isValid(),
            "sin(" + testCase.x + ") = " + testCase.expectedY + " should be valid: " + result.getErrorMessage());
        assertEquals(testCase.expectedY, result.getExpectedValue(), 1e-15);
    }

    @ParameterizedTest
    @MethodSource("cosTabularValues")
    void shouldValidateCosValues(TabularTestCase testCase) {
        TabularValidator.ValidationResult result =
            TabularValidator.validateValue(ModuleType.COS, testCase.x, testCase.expectedY);

        assertTrue(result.isValid(),
            "cos(" + testCase.x + ") = " + testCase.expectedY + " should be valid: " + result.getErrorMessage());
        assertEquals(testCase.expectedY, result.getExpectedValue(), 1e-15);
    }

    @ParameterizedTest
    @MethodSource("tanTabularValues")
    void shouldValidateTanValues(TabularTestCase testCase) {
        TabularValidator.ValidationResult result =
            TabularValidator.validateValue(ModuleType.TAN, testCase.x, testCase.expectedY);

        assertTrue(result.isValid(),
            "tan(" + testCase.x + ") = " + testCase.expectedY + " should be valid: " + result.getErrorMessage());
        assertEquals(testCase.expectedY, result.getExpectedValue(), 1e-15);
    }

    @ParameterizedTest
    @MethodSource("lnTabularValues")
    void shouldValidateLnValues(TabularTestCase testCase) {
        TabularValidator.ValidationResult result =
            TabularValidator.validateValue(ModuleType.LN, testCase.x, testCase.expectedY);

        assertTrue(result.isValid(),
            "ln(" + testCase.x + ") = " + testCase.expectedY + " should be valid: " + result.getErrorMessage());
        assertEquals(testCase.expectedY, result.getExpectedValue(), 1e-15);
    }

    @Test
    void shouldDetectInvalidValues() {
        // Намеренно неправильное значение sin(0) = 0.5 (должно быть 0.0)
        TabularValidator.ValidationResult result =
            TabularValidator.validateValue(ModuleType.SIN, 0.0, 0.5);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("expected=0"));
        assertTrue(result.getErrorMessage().contains("actual=0.5"));
        assertEquals(0.0, result.getExpectedValue(), 1e-15);
        assertEquals(0.5, result.getActualValue(), 1e-15);
    }

    @Test
    void shouldHandleValuesWithoutTabularData() {
        // Значение, для которого нет табличных данных
        TabularValidator.ValidationResult result =
            TabularValidator.validateValue(ModuleType.SIN, 1.23456, 0.94249);

        assertTrue(result.isValid()); // Должно быть valid, так как нет данных для сравнения
        assertTrue(result.getErrorMessage().contains("No tabular data for x="));
        assertTrue(Double.isNaN(result.getExpectedValue()));
    }

    @Test
    void shouldValidateMultipleValues() {
        List<Double> xValues = Arrays.asList(0.0, Math.PI/6, Math.PI/4);
        List<Double> yValues = Arrays.asList(0.0, 0.5, Math.sqrt(2)/2);

        List<TabularValidator.ValidationResult> results =
            TabularValidator.validateValues(ModuleType.SIN, xValues, yValues);

        assertEquals(3, results.size());
        assertTrue(results.get(0).isValid());
        assertTrue(results.get(1).isValid());
        assertTrue(results.get(2).isValid());
    }

    @Test
    void shouldThrowOnMismatchedListSizes() {
        List<Double> xValues = Arrays.asList(0.0, Math.PI/6);
        List<Double> yValues = Arrays.asList(0.0); // Размер не совпадает

        assertThrows(IllegalArgumentException.class, () ->
            TabularValidator.validateValues(ModuleType.SIN, xValues, yValues));
    }

    @Test
    void shouldNormalizeAnglesForTrigFunctions() {
        // sin(2π) должно быть эквивалентно sin(0)
        TabularValidator.ValidationResult result =
            TabularValidator.validateValue(ModuleType.SIN, 2 * Math.PI, 0.0);

        assertTrue(result.isValid());
        assertEquals(0.0, result.getExpectedValue(), 1e-15);

        // sin(-π) должно быть эквивалентно sin(π)
        TabularValidator.ValidationResult result2 =
            TabularValidator.validateValue(ModuleType.SIN, -Math.PI, 0.0);

        assertTrue(result2.isValid());
        assertEquals(0.0, result2.getExpectedValue(), 1e-15);
    }

    @Test
    void shouldCheckTabularValueAvailability() {
        assertTrue(TabularValidator.hasTabularValue(ModuleType.SIN, 0.0));
        assertTrue(TabularValidator.hasTabularValue(ModuleType.SIN, Math.PI/4));
        assertFalse(TabularValidator.hasTabularValue(ModuleType.SIN, 1.23456));
        assertFalse(TabularValidator.hasTabularValue(ModuleType.COT, 0.0)); // Нет таблицы для COT
    }

    @Test
    void shouldGetTabularXValues() {
        List<Double> sinXValues = TabularValidator.getTabularXValues(ModuleType.SIN);
        assertFalse(sinXValues.isEmpty());
        assertTrue(sinXValues.contains(0.0));
        assertTrue(sinXValues.contains(Math.PI/4));

        List<Double> cotXValues = TabularValidator.getTabularXValues(ModuleType.COT);
        assertTrue(cotXValues.isEmpty()); // Нет таблицы для COT
    }

    @Test
    void shouldValidateWithRealFunctionImplementations() {
        // Интеграционный тест с реальными функциями
        UnaryFunction sinFunction = new SinFunction(EPS, MAX_ITERATIONS);
        UnaryFunction cosFunction = new CosFunction(EPS, MAX_ITERATIONS);

        List<Double> tabularXValues = TabularValidator.getTabularXValues(ModuleType.SIN);

        for (double x : tabularXValues) {
            double computedSin = sinFunction.apply(x);
            TabularValidator.ValidationResult sinResult =
                TabularValidator.validateValue(ModuleType.SIN, x, computedSin);

            assertTrue(sinResult.isValid(),
                "SinFunction result should match tabular value for x=" + x + ": " + sinResult.getErrorMessage());
        }

        tabularXValues = TabularValidator.getTabularXValues(ModuleType.COS);
        for (double x : tabularXValues) {
            double computedCos = cosFunction.apply(x);
            TabularValidator.ValidationResult cosResult =
                TabularValidator.validateValue(ModuleType.COS, x, computedCos);

            assertTrue(cosResult.isValid(),
                "CosFunction result should match tabular value for x=" + x + ": " + cosResult.getErrorMessage());
        }
    }

    // Вспомогательный класс для параметризованных тестов
    record TabularTestCase(double x, double expectedY) {}

    // Тестовые данные для sin
    static Stream<TabularTestCase> sinTabularValues() {
        return Stream.of(
            new TabularTestCase(0.0, 0.0),
            new TabularTestCase(Math.PI/6, 0.5),
            new TabularTestCase(Math.PI/4, Math.sqrt(2)/2.0),
            new TabularTestCase(Math.PI/3, Math.sqrt(3)/2.0),
            new TabularTestCase(Math.PI/2, 1.0),
            new TabularTestCase(Math.PI, 0.0),
            new TabularTestCase(-Math.PI/6, -0.5),
            new TabularTestCase(-Math.PI/4, -Math.sqrt(2)/2.0)
        );
    }

    // Тестовые данные для cos
    static Stream<TabularTestCase> cosTabularValues() {
        return Stream.of(
            new TabularTestCase(0.0, 1.0),
            new TabularTestCase(Math.PI/6, Math.sqrt(3)/2.0),
            new TabularTestCase(Math.PI/4, Math.sqrt(2)/2.0),
            new TabularTestCase(Math.PI/3, 0.5),
            new TabularTestCase(Math.PI/2, 0.0),
            new TabularTestCase(Math.PI, -1.0),
            new TabularTestCase(-Math.PI/6, Math.sqrt(3)/2.0),
            new TabularTestCase(-Math.PI/4, Math.sqrt(2)/2.0)
        );
    }

    // Тестовые данные для tan
    static Stream<TabularTestCase> tanTabularValues() {
        return Stream.of(
            new TabularTestCase(0.0, 0.0),
            new TabularTestCase(Math.PI/6, 1.0/Math.sqrt(3)),
            new TabularTestCase(Math.PI/4, 1.0),
            new TabularTestCase(Math.PI/3, Math.sqrt(3)),
            new TabularTestCase(Math.PI, 0.0),
            new TabularTestCase(-Math.PI/6, -1.0/Math.sqrt(3)),
            new TabularTestCase(-Math.PI/4, -1.0)
        );
    }

    // Тестовые данные для ln
    static Stream<TabularTestCase> lnTabularValues() {
        return Stream.of(
            new TabularTestCase(1.0, 0.0),
            new TabularTestCase(Math.E, 1.0),
            new TabularTestCase(2.0, Math.log(2)),
            new TabularTestCase(0.5, -Math.log(2))
        );
    }
}