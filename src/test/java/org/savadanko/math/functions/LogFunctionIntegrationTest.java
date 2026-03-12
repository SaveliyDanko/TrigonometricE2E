package org.savadanko.math.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LogFunctionIntegrationTest extends IntegrationTestSupport {

    private final UnaryFunction ln = new LnFunction(EPS, MAX_ITERATIONS);
    private final UnaryFunction log2 = new LogFunction(ln, 2.0, EPS);
    private final UnaryFunction log3 = new LogFunction(ln, 3.0, EPS);

    @ParameterizedTest
    @ValueSource(doubles = {0.25, 0.5, 2.0, 3.0, 10.0})
    void shouldCalculateLog2UsingRealLn(double x) {
        assertClose(Math.log(x) / Math.log(2.0), log2.apply(x));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.25, 0.5, 2.0, 3.0, 10.0})
    void shouldCalculateLog3UsingRealLn(double x) {
        assertClose(Math.log(x) / Math.log(3.0), log3.apply(x));
    }

    @Test
    void shouldReturnNaNForNegativeArgument() {
        assertTrue(Double.isNaN(log2.apply(-1.0)));
    }
}
