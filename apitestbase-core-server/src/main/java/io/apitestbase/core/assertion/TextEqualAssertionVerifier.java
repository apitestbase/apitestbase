package io.apitestbase.core.assertion;

import io.apitestbase.models.TestResult;
import io.apitestbase.models.assertion.AssertionVerificationResult;
import io.apitestbase.models.assertion.TextEqualAssertionProperties;

public class TextEqualAssertionVerifier extends AssertionVerifier {
    /**
     *
     * @param inputs contains only one argument: the string that the assertion is verified against
     * @return
     */
    @Override
    public AssertionVerificationResult verify(Object... inputs) {
        TextEqualAssertionProperties assertionProperties =
                (TextEqualAssertionProperties) getAssertion().getOtherProperties();
        String expectedText = assertionProperties.getExpectedText();

        //  validate argument
        if (expectedText == null) {
            throw new IllegalArgumentException("Expected Text not specified");
        }

        AssertionVerificationResult result = new AssertionVerificationResult();
        String actualText = (String) inputs[0];
        result.setResult(expectedText.equals(actualText) ? TestResult.PASSED : TestResult.FAILED);
        return result;
    }
}
