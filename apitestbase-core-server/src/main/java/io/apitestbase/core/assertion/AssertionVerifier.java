package io.apitestbase.core.assertion;

import io.apitestbase.models.assertion.Assertion;
import io.apitestbase.models.assertion.AssertionVerificationResult;

public abstract class AssertionVerifier {
    private Assertion assertion;

    protected Assertion getAssertion() {
        return assertion;
    }

    protected void setAssertion(Assertion assertion) {
        this.assertion = assertion;
    }

    /**
     * @param inputs the objects that the assertion is verified against.
     * @return
     * @throws Exception
     */
    public abstract AssertionVerificationResult verify(Object ...inputs) throws Exception;
}
