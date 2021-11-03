package io.apitestbase.core.testcase;

import com.rits.cloning.Cloner;
import io.apitestbase.core.propertyextractor.PropertyExtractorRunner;
import io.apitestbase.core.propertyextractor.PropertyExtractorRunnerFactory;
import io.apitestbase.core.teststep.BasicTeststepRun;
import io.apitestbase.core.teststep.HTTPAPIResponse;
import io.apitestbase.core.teststep.TeststepRunnerFactory;
import io.apitestbase.db.UtilsDAO;
import io.apitestbase.models.DataTable;
import io.apitestbase.models.DataTableCell;
import io.apitestbase.models.DataTableColumn;
import io.apitestbase.models.TestResult;
import io.apitestbase.models.assertion.AssertionVerification;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.propertyextractor.PropertyExtractor;
import io.apitestbase.models.testrun.*;
import io.apitestbase.models.teststep.HTTPHeader;
import io.apitestbase.models.teststep.Teststep;
import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.*;

import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_DATE_TIME_FORMAT;
import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME;

public class TestcaseStepRunner {
    private Logger LOGGER;

    TestcaseStepRunner(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    TeststepRun run(Teststep teststep, UtilsDAO utilsDAO, Map<String, String> referenceableStringProperties,
                    Map<String, Endpoint> referenceableEndpointProperties, TestcaseRunContext testcaseRunContext) throws IOException {
        TeststepRun stepRun;

        //  test step run starts
        Date stepRunStartTime = new Date();
        referenceableStringProperties.put(IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME,
                IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(stepRunStartTime));

        if (teststep.getDataTable() == null || teststep.getDataTable().getRows().isEmpty()) {
            RegularTeststepRun regularTeststepRun = new RegularTeststepRun();
            regularTeststepRun.setStartTime(stepRunStartTime);
            TestResult result = runAtomicStep(regularTeststepRun.getAtomicRunResult(), teststep, utilsDAO,
                    referenceableStringProperties, referenceableEndpointProperties, testcaseRunContext);
            regularTeststepRun.setResult(result);
            stepRun = regularTeststepRun;
        } else {
            stepRun = runDataDrivenTeststep(stepRunStartTime, teststep, utilsDAO, referenceableStringProperties,
                    referenceableEndpointProperties, testcaseRunContext);
        }

        //  test step run ends
        stepRun.setDuration(new Date().getTime() - stepRun.getStartTime().getTime());

        return stepRun;
    }

    private TestResult runAtomicStep(TeststepAtomicRunResult atomicRunResult, Teststep teststep, UtilsDAO utilsDAO,
                                     Map<String, String> referenceableStringProperties,
                                     Map<String, Endpoint> referenceableEndpointProperties,
                                     TestcaseRunContext testcaseRunContext)
            throws IOException {
        TestResult result = TestResult.PASSED;
        BasicTeststepRun basicTeststepRun;
        boolean exceptionOccurred = false;  //  use this flag instead of checking stepRun.getErrorMessage() != null, for code clarity
        Teststep clonedTeststep = new Cloner().deepClone(teststep);
        atomicRunResult.setTeststep(clonedTeststep);
        try {
            basicTeststepRun = TeststepRunnerFactory.getInstance().newTeststepRunner(
                    clonedTeststep, utilsDAO, referenceableStringProperties, referenceableEndpointProperties,
                    testcaseRunContext).run();
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
            referenceableStringProperties.putAll(extractedProperties);
        }

        return result;
    }

    private DataDrivenTeststepRun runDataDrivenTeststep(Date stepRunStartTime, Teststep teststep, UtilsDAO utilsDAO,
                                                        Map<String, String> referenceableStringProperties,
                                                        Map<String, Endpoint> referenceableEndpointProperties, TestcaseRunContext testcaseRunContext) throws IOException {
        DataDrivenTeststepRun stepRun = new DataDrivenTeststepRun();
        stepRun.setResult(TestResult.PASSED);
        stepRun.setStartTime(stepRunStartTime);
        DataTable dataTable = teststep.getDataTable();
//        GeneralUtils.checkDuplicatePropertyNameBetweenDataTableAndUPDs(getUdpNames(), dataTable);

        for (int dataTableRowIndex = 0; dataTableRowIndex < dataTable.getRows().size(); dataTableRowIndex++) {
            LinkedHashMap<String, DataTableCell> dataTableRow = dataTable.getRows().get(dataTableRowIndex);
            TeststepIndividualRun individualRun = new TeststepIndividualRun();
            stepRun.getIndividualRuns().add(individualRun);

            //  test step individual run starts
            individualRun.setStartTime(new Date());
            individualRun.setCaption(dataTableRow.get(DataTableColumn.COLUMN_NAME_CAPTION).getValue());
            LOGGER.info("Start individually running test step with data table row: " + individualRun.getCaption());
            referenceableStringProperties.putAll(dataTable.getStringPropertiesInRow(dataTableRowIndex));

            individualRun.setResult(runAtomicStep(
                    individualRun.getAtomicRunResult(), teststep, utilsDAO, referenceableStringProperties,
                    referenceableEndpointProperties, testcaseRunContext));

            //  test step individual run ends
            individualRun.setDuration(new Date().getTime() - individualRun.getStartTime().getTime());
            LOGGER.info("Finish individually running test step with data table row: " + individualRun.getCaption());
        }

        for (TeststepIndividualRun individualRun: stepRun.getIndividualRuns()) {
            if (TestResult.FAILED == individualRun.getResult()) {
                stepRun.setResult(TestResult.FAILED);
                break;
            }
        }

        return stepRun;
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
