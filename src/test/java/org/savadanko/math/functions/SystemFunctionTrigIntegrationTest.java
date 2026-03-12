package org.savadanko.math.functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SystemFunctionTrigIntegrationTest extends IntegrationTestSupport {

    @Mock
    private UnaryFunction log2;
    @Mock
    private UnaryFunction log3;
    @Mock
    private UnaryFunction log5;
    @Mock
    private UnaryFunction log10;

    private SystemFunction system;

    @BeforeEach
    void setUp() {
        UnaryFunction sin = new SinFunction(EPS, MAX_ITERATIONS);
        UnaryFunction cos = new CosFunction(EPS, MAX_ITERATIONS);

        UnaryFunction tan = new TanFunction(sin, cos, EPS);
        UnaryFunction cot = new CotFunction(tan, EPS);
        UnaryFunction sec = new SecFunction(cos, EPS);

        system = new SystemFunction(cot, tan, sec, log2, log3, log5, log10, EPS);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-2.0, -1.0, -0.5, -0.25})
    void shouldCalculateNegativeBranchUsingRealTrigModules(double x) {
        assertClose(ReferenceMath.trigBranch(x), system.apply(x));
        verifyNoInteractions(log2, log3, log5, log10);
    }

    @Test
    void shouldReturnNaNAtZero() {
        assertTrue(Double.isNaN(system.apply(0.0)));
        verifyNoInteractions(log2, log3, log5, log10);
    }
}
