package io.apitestbase.core.assertion;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.apitestbase.models.TestResult;
import io.apitestbase.models.assertion.AssertionVerificationResult;
import io.apitestbase.models.assertion.HTTPStubHitAssertionProperties;
import io.apitestbase.models.assertion.HTTPStubHitAssertionVerificationResult;

import java.util.List;
import java.util.UUID;

public class HTTPStubHitAssertionVerifier extends AssertionVerifier {
    @Override
    public AssertionVerificationResult verify(Object ...inputs) {
        HTTPStubHitAssertionVerificationResult result = new HTTPStubHitAssertionVerificationResult();

        HTTPStubHitAssertionProperties otherProperties = (HTTPStubHitAssertionProperties) getAssertion().getOtherProperties();

        List<ServeEvent> allServeEvents = (List<ServeEvent>) inputs[0];
        UUID stubInstanceUUID = (UUID) inputs[1];
        short stubInstanceHitCount = 0;
        for (ServeEvent serveEvent: allServeEvents) {
            if (serveEvent.getStubMapping().getId().equals(stubInstanceUUID)) {    //  the stub instance has been hit
                stubInstanceHitCount++;
            }
        }
        result.setActualHitCount(stubInstanceHitCount);

        if (stubInstanceHitCount != otherProperties.getExpectedHitCount()) {
            result.setResult(TestResult.FAILED);
        } else {
            result.setResult(TestResult.PASSED);
        }

        return result;
    }
}
