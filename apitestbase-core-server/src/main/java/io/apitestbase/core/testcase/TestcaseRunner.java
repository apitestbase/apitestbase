package io.apitestbase.core.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.apitestbase.db.TestcaseRunDAO;
import io.apitestbase.db.UtilsDAO;
import io.apitestbase.models.HTTPStubMapping;
import io.apitestbase.models.TestResult;
import io.apitestbase.models.Testcase;
import io.apitestbase.models.assertion.Assertion;
import io.apitestbase.models.assertion.HTTPStubHitAssertionProperties;
import io.apitestbase.models.assertion.HTTPStubsHitInOrderAssertionProperties;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.testrun.testcaserun.TestcaseRun;
import io.apitestbase.models.testrun.teststeprun.TeststepRun;
import io.apitestbase.models.teststep.HTTPStubsSetupTeststepProperties;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.utils.GeneralUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_DATE_TIME_FORMAT;
import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_NAME_TEST_CASE_START_TIME;

public abstract class TestcaseRunner {
    private Testcase testcase;
    private UtilsDAO utilsDAO;
    private TestcaseRunDAO testcaseRunDAO;
    private Logger LOGGER;
    private TestcaseRunContext testcaseRunContext = new TestcaseRunContext();

    TestcaseRunner(Testcase testcase, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO, Logger LOGGER, WireMockServer wireMockServer) {
        this.testcase = testcase;
        this.utilsDAO = utilsDAO;
        this.testcaseRunDAO = testcaseRunDAO;
        this.LOGGER = LOGGER;
        this.testcaseRunContext.setWireMockServer(wireMockServer);
    }

    Testcase getTestcase() {
        return testcase;
    }

    TestcaseRunDAO getTestcaseRunDAO() {
        return testcaseRunDAO;
    }

    TestcaseRunContext getTestcaseRunContext() {
        return testcaseRunContext;
    }

    public abstract TestcaseRun run() throws Exception;

    //  process the test case before starting to run it
    void preProcessing() {
        if (!testcase.getHttpStubMappings().isEmpty()) {
            //  add HTTPStubsSetup step
            Teststep httpStubsSetupStep = new Teststep(Teststep.TYPE_HTTP_STUBS_SETUP);
            httpStubsSetupStep.setName("Set up HTTP stubs");
            HTTPStubsSetupTeststepProperties stubsSetupTeststepProperties = new HTTPStubsSetupTeststepProperties();
            stubsSetupTeststepProperties.setHttpStubMappings(testcase.getHttpStubMappings());
            httpStubsSetupStep.setOtherProperties(stubsSetupTeststepProperties);
            testcase.getTeststeps().add(0, httpStubsSetupStep);

            //  add HTTPStubRequestsCheck step and its assertions
            Teststep stubRequestsCheckStep = new Teststep(Teststep.TYPE_HTTP_STUB_REQUESTS_CHECK);
            testcase.getTeststeps().add(testcase.getTeststeps().size(), stubRequestsCheckStep);
            stubRequestsCheckStep.setName("Check HTTP stub requests");
            for (HTTPStubMapping stub: testcase.getHttpStubMappings()) {
                Assertion stubHitAssertion = new Assertion(Assertion.TYPE_HTTP_STUB_HIT);
                stubHitAssertion.setName("Stub was hit");
                stubHitAssertion.setOtherProperties(
                        new HTTPStubHitAssertionProperties(stub.getNumber(), stub.getExpectedHitCount()));
                stubRequestsCheckStep.getAssertions().add(stubHitAssertion);
            }
            if (testcase.getHttpStubMappings().size() > 1 && testcase.isCheckHTTPStubsHitOrder()) {
                Assertion stubsHitInOrderAssertion = new Assertion(Assertion.TYPE_HTTP_STUBS_HIT_IN_ORDER);
                stubsHitInOrderAssertion.setName("Stubs were hit in order");
                List<Short> expectedHitOrder = new ArrayList<>();
                for (HTTPStubMapping stub: testcase.getHttpStubMappings()) {
                    expectedHitOrder.add(stub.getNumber());
                }
                stubsHitInOrderAssertion.setOtherProperties(new HTTPStubsHitInOrderAssertionProperties(expectedHitOrder));
                stubRequestsCheckStep.getAssertions().add(stubsHitInOrderAssertion);
            }
            Assertion allStubRequestsMatchedAssertion = new Assertion(Assertion.TYPE_ALL_HTTP_STUB_REQUESTS_MATCHED);
            allStubRequestsMatchedAssertion.setName("All stub requests were matched");
            stubRequestsCheckStep.getAssertions().add(allStubRequestsMatchedAssertion);
        }

        for (Teststep teststep : testcase.getTeststeps()) {
            if (Teststep.TYPE_IIB.equals(teststep.getType()) &&
                    Teststep.ACTION_WAIT_FOR_PROCESSING_COMPLETION.equals(teststep.getAction())) {
                Teststep waitUntilNextSecondStep = new Teststep(Teststep.TYPE_WAIT_UNTIL_NEXT_SECOND);
                waitUntilNextSecondStep.setName("Wait until next second");
                testcase.getTeststeps().add(0, waitUntilNextSecondStep);
                break;
            }
        }
    }

    void startTestcaseRun(TestcaseRun testcaseRun) {
        Date testcaseRunStartTime = new Date();
        LOGGER.info("Start running test case: " + testcase.getName());

        testcaseRun.setTestcaseId(testcase.getId());
        testcaseRun.setTestcaseName(testcase.getName());
        testcaseRun.setTestcaseFolderPath(testcase.getFolderPath());
        testcaseRun.setResult(TestResult.PASSED);
        testcaseRun.setStartTime(testcaseRunStartTime);
        testcaseRunContext.setTestcaseRunStartTime(testcaseRunStartTime);

        Map<String, String> udpMap= GeneralUtils.udpListToMap(testcase.getUdps());
        testcaseRunContext.getReferenceableStringProperties().putAll(udpMap);
        testcaseRunContext.getReferenceableStringProperties().put(IMPLICIT_PROPERTY_NAME_TEST_CASE_START_TIME,
                IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(testcaseRunStartTime));
    }

    TeststepRun runTeststep(Teststep teststep, TestcaseIndividualRunContext testcaseIndividualRunContext)
            throws IOException, InterruptedException {
        Map<String, String> referenceableStringProperties = testcaseIndividualRunContext == null ?
                getTestcaseRunContext().getReferenceableStringProperties() :
                testcaseIndividualRunContext.getReferenceableStringProperties();
        Map<String, Endpoint> referenceableEndpointProperties = testcaseIndividualRunContext == null ?
                getTestcaseRunContext().getReferenceableEndpointProperties() :
                testcaseIndividualRunContext.getReferenceableEndpointProperties();
        Map<String, String> referenceableStringPropertiesShallowCopy = new HashMap<>(referenceableStringProperties);
        return new TestcaseStepRunner(LOGGER).run(teststep, utilsDAO, referenceableStringPropertiesShallowCopy,
                referenceableEndpointProperties, testcaseRunContext, testcaseIndividualRunContext);
    }
}
