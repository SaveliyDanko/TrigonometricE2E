package org.savadanko.math.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TanFunctionIntegrationTest extends IntegrationTestSupport {

    private final UnaryFunction tan = new TanFunction(
            new SinFunction(EPS, MAX_ITERATIONS),
            new CosFunction(EPS, MAX_ITERATIONS),
            EPS
    );

    @ParameterizedTest
    @ValueSource(doubles = {-2.0, -1.0, -0.5, 0.5, 1.0, 2.0})
    void shouldCalculateTanUsingRealSinAndCos(double x) {
        assertClose(Math.tan(x), tan.apply(x));
    }

    @Test
    void shouldReturnNaNNearSingularity() {
        assertTrue(Double.isNaN(tan.apply(Math.PI / 2)));
    }
}
