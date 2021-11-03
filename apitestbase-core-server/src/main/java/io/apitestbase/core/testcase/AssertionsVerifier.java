package io.apitestbase.core.testcase;

import io.apitestbase.core.assertion.AssertionVerifier;
import io.apitestbase.core.assertion.AssertionVerifierFactory;
import io.apitestbase.core.teststep.*;
import io.apitestbase.models.TestResult;
import io.apitestbase.models.assertion.Assertion;
import io.apitestbase.models.assertion.AssertionVerification;
import io.apitestbase.models.assertion.AssertionVerificationResult;
import io.apitestbase.models.assertion.HTTPStubHitAssertionProperties;
import io.apitestbase.models.teststep.Teststep;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssertionsVerifier {
    private Object resolveAssertionVerificationInputFromAPIResponse(String teststepType, String teststepAction,
                                                                    String assertionType, Object apiResponse) {
        Object result = apiResponse;

        if (Assertion.TYPE_STATUS_CODE_EQUAL.equals(assertionType)) {
            result = ((HTTPAPIResponse) apiResponse).getStatusCode();
        } else if (Teststep.TYPE_SOAP.equals(teststepType) || Teststep.TYPE_HTTP.equals(teststepType)) {
            result = ((HTTPAPIResponse) apiResponse).getHttpBody();
        } else if (Assertion.TYPE_HTTP_STUB_HIT.equals(assertionType) ||
                Assertion.TYPE_ALL_HTTP_STUB_REQUESTS_MATCHED.equals(assertionType) ||
                Assertion.TYPE_HTTP_STUBS_HIT_IN_ORDER.equals(assertionType)) {
            result = ((WireMockServerAPIResponse) apiResponse).getAllServeEvents();
        } else if (Teststep.TYPE_DB.equals(teststepType)) {
            result = ((DBAPIResponse) apiResponse).getRowsJSON();
        } else if (Teststep.TYPE_JMS.equals(teststepType)) {
            if (Teststep.ACTION_CHECK_DEPTH.equals(teststepAction)) {
                result = ((JMSCheckQueueDepthResponse) apiResponse).getQueueDepth();
            } else if (Teststep.ACTION_BROWSE.equals(teststepAction)) {
                result = apiResponse == null ? null : ((JMSBrowseQueueResponse) apiResponse).getBody();
            }
        } else if (Teststep.TYPE_MQ.equals(teststepType)) {
            if (Teststep.ACTION_CHECK_DEPTH.equals(teststepAction)) {
                result = ((MQCheckQueueDepthResponse) apiResponse).getQueueDepth();
            } else if (Teststep.ACTION_DEQUEUE.equals(teststepAction)) {
                MQDequeueResponse mqDequeueResponse = (MQDequeueResponse) apiResponse;
                if (mqDequeueResponse == null) {
                    result = null;
                } else {
                    if (Assertion.TYPE_HAS_AN_MQRFH2_FOLDER_EQUAL_TO_XML.equals(assertionType)) {
                        result = mqDequeueResponse.getMqrfh2Header();
                    } else {
                        result = mqDequeueResponse.getBodyAsText();
                    }
                }
            }
        }

        return result;
    }

    /**
     * Verify assertions against the API response.
     * @param testcaseRunContext
     * @param referenceableStringProperties
     * @param LOGGER
     * @param teststepType
     * @param teststepAction
     * @param assertions
     * @param apiResponse
     */
    protected List<AssertionVerification> verifyAssertions(
            TestcaseRunContext testcaseRunContext, Map<String, String> referenceableStringProperties, Logger LOGGER,
            String teststepType, String teststepAction, List<Assertion> assertions, Object apiResponse) throws IOException {
        List<AssertionVerification> assertionVerifications = new ArrayList<>();
        for (Assertion assertion : assertions) {
            Object assertionVerificationInput = resolveAssertionVerificationInputFromAPIResponse(teststepType,
                    teststepAction, assertion.getType(), apiResponse);

            //  resolve assertion verification input2 if applicable
            Object assertionVerificationInput2 = null;
            if (Assertion.TYPE_HTTP_STUB_HIT.equals(assertion.getType())) {
                HTTPStubHitAssertionProperties otherProperties = (HTTPStubHitAssertionProperties) assertion.getOtherProperties();
                assertionVerificationInput2 = testcaseRunContext.getHttpStubMappingInstanceIds().get(otherProperties.getStubNumber());
            }

            AssertionVerification verification = new AssertionVerification();
            assertionVerifications.add(verification);
            verification.setAssertion(assertion);

            AssertionVerifier verifier = AssertionVerifierFactory.getInstance().create(
                    assertion, referenceableStringProperties);
            AssertionVerificationResult verificationResult;
            try {
                verificationResult = verifier.verify(assertionVerificationInput, assertionVerificationInput2);
            } catch (Exception e) {
                LOGGER.error("Failed to verify assertion", e);
                verificationResult = new AssertionVerificationResult();
                verificationResult.setResult(TestResult.FAILED);
                String message = e.getMessage();
                verificationResult.setError(message == null ? "null" : message);  // exception message could be null (though rarely)
            }

            verification.setVerificationResult(verificationResult);
        }

        return assertionVerifications;
    }
}
