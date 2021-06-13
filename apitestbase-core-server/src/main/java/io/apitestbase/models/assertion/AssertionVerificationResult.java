package io.apitestbase.models.assertion;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.apitestbase.models.TestResult;

/**
 * Output of assertion verifier.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, property = "minClassName")
public class AssertionVerificationResult {
    private TestResult result;
    private String error;            //  message of error occurred during verification

    public TestResult getResult() {
        return result;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
