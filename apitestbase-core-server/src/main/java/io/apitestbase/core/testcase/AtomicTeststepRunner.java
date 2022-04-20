package io.apitestbase.core.testcase;

import com.rits.cloning.Cloner;
import io.apitestbase.core.propertyextractor.PropertyExtractorRunner;
import io.apitestbase.core.propertyextractor.PropertyExtractorRunnerFactory;
import io.apitestbase.core.teststep.TeststepActionRunResult;
import io.apitestbase.core.teststep.HTTPAPIResponse;
import io.apitestbase.core.teststep.TeststepActionRunnerFactory;
import io.apitestbase.db.UtilsDAO;
import io.apitestbase.models.TestResult;
import io.apitestbase.models.assertion.AssertionVerification;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.propertyextractor.PropertyExtractor;
import io.apitestbase.models.testrun.teststeprun.TeststepAtomicRunResult;
import io.apitestbase.models.teststep.HTTPHeader;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.utils.GeneralUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AtomicTeststepRunner {
    public TestResult run(Logger LOGGER, TeststepAtomicRunResult atomicRunResult, Teststep teststep, UtilsDAO utilsDAO,
                          Map<String, String> referenceableStringProperties,
                          Map<String, Endpoint> referenceableEndpointProperties,
                          TestcaseRunContext testcaseRunContext,
                          TestcaseIndividualRunContext testcaseIndividualRunContext)
            throws IOException {
        TestResult result = TestResult.PASSED;
        TeststepActionRunResult basicTeststepRun;
        boolean exceptionOccurred = false;  //  use this flag instead of checking stepRun.getErrorMessage() != null, for code clarity
        Teststep clonedTeststep = new Cloner().deepClone(teststep);
        atomicRunResult.setTeststep(clonedTeststep);
        try {
            basicTeststepRun = TeststepActionRunnerFactory.getInstance().newTeststepActionRunner(
                    clonedTeststep, utilsDAO, referenceableStringProperties, referenceableEndpointProperties,
                    testcaseRunContext, testcaseIndividualRunContext).run();
            atomicRunResult.setResponse(basicTeststepRun.getResponse());
            atomicRunResult.setInfoMessage(basicTeststepRun.getInfoMessage());
        } catch (Exception e) {
            exceptionOccurred = true;
            String message = e.getMessage();
            atomicRunResult.setErrorMessage(message == null ? "null" : message);  // exception message could be null (though rarely)
            LOGGER.error(message, e);
        }

        if (exceptionOccurred) {
            result = TestResult.FAILED;
        } else {
            Object apiResponse = atomicRunResult.getResponse();

            //  verify assertions against the API response
            List<AssertionVerification> assertionVerifications = new AssertionsVerifier().verifyAssertions(
                    testcaseRunContext, referenceableStringProperties, LOGGER, clonedTeststep.getType(),
                    clonedTeststep.getAction(), clonedTeststep.getAssertions(), apiResponse);
            atomicRunResult.setAssertionVerifications(assertionVerifications);
            for (AssertionVerification assertionVerification: assertionVerifications) {
                if (TestResult.FAILED == assertionVerification.getVerificationResult().getResult()) {
                    result = TestResult.FAILED;
                }
            }

            //  extract properties out of the API response
            Map<String, String> extractedProperties = new HashMap<>();
            try {
                extractedProperties = extractPropertiesOutOfAPIResponse(clonedTeststep.getType(),
                        clonedTeststep.getPropertyExtractors(), apiResponse, referenceableStringProperties);
            } catch (Exception e) {
                String errorMessage = "Failed to extract properties out of API response.";
                LOGGER.error(errorMessage, e);
                atomicRunResult.setErrorMessage(errorMessage + " " + e.getMessage());
                result = TestResult.FAILED;
            }
            if (testcaseIndividualRunContext != null) {    //  in data driven test case individual run
                testcaseIndividualRunContext.getReferenceableStringProperties().putAll(extractedProperties);
            } else {                                       //  in regular test case run
                testcaseRunContext.getReferenceableStringProperties().putAll(extractedProperties);
            }
        }

        return result;
    }

    /**
     * Extract properties out of API response, and make the properties visible to the next test step run.
     */
    private Map<String, String> extractPropertiesOutOfAPIResponse(String teststepType,
                                                                  List<PropertyExtractor> propertyExtractors,
                                                                  Object apiResponse,
                                                                  Map<String, String> referenceableStringProperties) throws Exception {
        Map<String, String> extractedProperties = new HashMap<>();
        for (PropertyExtractor propertyExtractor: propertyExtractors) {
            String propertyExtractionInput = null;
            if (Teststep.TYPE_HTTP.equals(teststepType)) {
                HTTPAPIResponse httpApiResponse = (HTTPAPIResponse) apiResponse;
                if (PropertyExtractor.TYPE_COOKIE.equals(propertyExtractor.getType())) {
                    Optional<HTTPHeader> setCookieHeader = httpApiResponse.getHttpHeaders().stream()
                            .filter(httpHeader -> HttpHeader.SET_COOKIE.asString().equals(httpHeader.getName())).findFirst();
                    propertyExtractionInput = setCookieHeader.isPresent() ? setCookieHeader.get().getValue() : null;
                } else {
                    propertyExtractionInput = httpApiResponse.getHttpBody();
                }
            }

            PropertyExtractorRunner propertyExtractorRunner = PropertyExtractorRunnerFactory.getInstance().create(
                    propertyExtractor, referenceableStringProperties);
            String propertyValue = propertyExtractorRunner.extract(propertyExtractionInput);
            extractedProperties.put(propertyExtractor.getPropertyName(), propertyValue);
        }

        return extractedProperties;
    }
}
