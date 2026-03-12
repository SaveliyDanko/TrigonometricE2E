package org.savadanko.math.functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemFunctionE2ETest extends IntegrationTestSupport {

    private final SystemFunction system = createRealSystem();

    @ParameterizedTest
    @ValueSource(doubles = {-2.0, -1.0, -0.5, -0.25, 0.25, 0.5, 2.0, 3.0, 10.0})
    void shouldMatchReferenceImplementation(double x) {
        assertClose(ReferenceMath.system(x), system.apply(x));
    }

    @Test
    void shouldReturnNaNAtSingularPoints() {
        assertTrue(Double.isNaN(system.apply(0.0)));
        assertTrue(Double.isNaN(system.apply(1.0)));
    }
}
