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
class SystemFunctionLogIntegrationTest extends IntegrationTestSupport {

    @Mock
    private UnaryFunction cot;
    @Mock
    private UnaryFunction tan;
    @Mock
    private UnaryFunction sec;

    private SystemFunction system;

    @BeforeEach
    void setUp() {
        UnaryFunction ln = new LnFunction(EPS, MAX_ITERATIONS);

        UnaryFunction log2 = new LogFunction(ln, 2.0, EPS);
        UnaryFunction log3 = new LogFunction(ln, 3.0, EPS);
        UnaryFunction log5 = new LogFunction(ln, 5.0, EPS);
        UnaryFunction log10 = new LogFunction(ln, 10.0, EPS);

        system = new SystemFunction(cot, tan, sec, log2, log3, log5, log10, EPS);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.25, 0.5, 2.0, 3.0, 10.0})
    void shouldCalculatePositiveBranchUsingRealLogModules(double x) {
        assertClose(ReferenceMath.logBranch(x), system.apply(x));
        verifyNoInteractions(cot, tan, sec);
    }

    @Test
    void shouldReturnNaNAtOne() {
        assertTrue(Double.isNaN(system.apply(1.0)));
        verifyNoInteractions(cot, tan, sec);
    }
}
