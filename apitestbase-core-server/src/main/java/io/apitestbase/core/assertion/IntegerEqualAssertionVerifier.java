package io.apitestbase.core.assertion;

import io.apitestbase.models.TestResult;
import io.apitestbase.models.assertion.AssertionVerificationResult;
import io.apitestbase.models.assertion.IntegerEqualAssertionProperties;
import io.apitestbase.models.assertion.IntegerEqualAssertionVerificationResult;

public class IntegerEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: the Integer that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult verify(Object ...inputs) {
        IntegerEqualAssertionVerificationResult result = new IntegerEqualAssertionVerificationResult();
        IntegerEqualAssertionProperties properties = (IntegerEqualAssertionProperties)
                getAssertion().getOtherProperties();
        result.setActualNumber((int) inputs[0]);
        result.setResult(Integer.valueOf(properties.getNumber()).equals(inputs[0]) ? TestResult.PASSED : TestResult.FAILED);

        return result;
    }
}
