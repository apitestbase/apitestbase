package io.apitestbase.core.assertion;

import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.apitestbase.models.TestResult;
import io.apitestbase.models.assertion.AssertionVerificationResult;
import io.apitestbase.models.assertion.HTTPStubsHitInOrderAssertionProperties;
import io.apitestbase.models.assertion.HTTPStubsHitInOrderAssertionVerificationResult;

import java.util.*;

import static io.apitestbase.APITestBaseConstants.WIREMOCK_STUB_METADATA_ATTR_NAME_API_TEST_BASE_NUMBER;

public class HTTPStubsHitInOrderAssertionVerifier extends AssertionVerifier {
    @Override
    public AssertionVerificationResult verify(Object... inputs) {
        HTTPStubsHitInOrderAssertionVerificationResult result = new HTTPStubsHitInOrderAssertionVerificationResult();
        HTTPStubsHitInOrderAssertionProperties otherProperties =
                (HTTPStubsHitInOrderAssertionProperties) getAssertion().getOtherProperties();

        Map<Date, Short> hitMap = new TreeMap<>();
        List<ServeEvent> allServeEvents = (List<ServeEvent>) inputs[0];
        for (ServeEvent serveEvent: allServeEvents) {
            if (serveEvent.getWasMatched()) {
                StubMapping stubMapping = serveEvent.getStubMapping();
                hitMap.put(serveEvent.getRequest().getLoggedDate(),
                        (Short) stubMapping.getMetadata().get(WIREMOCK_STUB_METADATA_ATTR_NAME_API_TEST_BASE_NUMBER));
            }
        }

        List<Short> actualHitOrder = new ArrayList(hitMap.values());
        result.setResult(otherProperties.getExpectedHitOrder().equals(actualHitOrder) ? TestResult.PASSED : TestResult.FAILED);
        result.setActualHitOrder(actualHitOrder);

        return result;
    }
}
