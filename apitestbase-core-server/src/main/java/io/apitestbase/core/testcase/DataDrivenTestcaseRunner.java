package io.apitestbase.core.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.apitestbase.db.TestcaseRunDAO;
import io.apitestbase.db.UtilsDAO;
import io.apitestbase.models.*;
import io.apitestbase.models.testrun.DataDrivenTestcaseRun;
import io.apitestbase.models.testrun.TestcaseIndividualRun;
import io.apitestbase.models.testrun.TestcaseRun;
import io.apitestbase.models.testrun.TeststepRun;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.utils.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_DATE_TIME_FORMAT;
import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_NAME_TEST_CASE_INDIVIDUAL_START_TIME;

public class DataDrivenTestcaseRunner extends TestcaseRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataDrivenTestcaseRunner.class);

    public DataDrivenTestcaseRunner(Testcase testcase, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO, WireMockServer wireMockServer) {
        super(testcase, utilsDAO, testcaseRunDAO, LOGGER, wireMockServer);
    }

    @Override
    public TestcaseRun run() throws IOException {
        DataDrivenTestcaseRun testcaseRun = new DataDrivenTestcaseRun();

        preProcessing();

        startTestcaseRun(testcaseRun);

        DataTable dataTable = getTestcase().getDataTable();
        Map<String, String> udpMap = GeneralUtils.udpListToMap(getTestcase().getUdps());
        GeneralUtils.checkDuplicatePropertyNames(udpMap.keySet(), dataTable.getNonCaptionColumnNames());

        for (int dataTableRowIndex = 0; dataTableRowIndex < dataTable.getRows().size(); dataTableRowIndex++) {
            LinkedHashMap<String, DataTableCell> dataTableRow = dataTable.getRows().get(dataTableRowIndex);

            TestcaseIndividualRunContext testcaseIndividualRunContext = new TestcaseIndividualRunContext();
            testcaseIndividualRunContext.getReferenceableStringProperties().putAll(
                    getTestcaseRunContext().getReferenceableStringProperties());
            testcaseIndividualRunContext.getReferenceableEndpointProperties().putAll(
                    getTestcaseRunContext().getReferenceableEndpointProperties());
            TestcaseIndividualRun individualRun = new TestcaseIndividualRun();
            testcaseRun.getIndividualRuns().add(individualRun);

            //  start test case individual run
            individualRun.setStartTime(new Date());
            individualRun.setCaption(dataTableRow.get(DataTableColumn.COLUMN_NAME_CAPTION).getValue());
            LOGGER.info("Start individually running test case with data table row: " + individualRun.getCaption());
            individualRun.setResult(TestResult.PASSED);
            testcaseIndividualRunContext.setTestcaseIndividualRunStartTime(individualRun.getStartTime());
            testcaseIndividualRunContext.getReferenceableStringProperties().put(IMPLICIT_PROPERTY_NAME_TEST_CASE_INDIVIDUAL_START_TIME,
                    IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(individualRun.getStartTime()));
            testcaseIndividualRunContext.getReferenceableEndpointProperties().putAll(
                    dataTable.getEndpointPropertiesInRow(dataTableRowIndex));
            testcaseIndividualRunContext.getReferenceableStringProperties().putAll(
                    dataTable.getStringPropertiesInRow(dataTableRowIndex));

            //  run test steps
            for (Teststep teststep : getTestcase().getTeststeps()) {
                individualRun.getStepRuns().add(runTeststep(teststep, testcaseIndividualRunContext));
            }

            //  test case individual run ends
            individualRun.setDuration(new Date().getTime() - individualRun.getStartTime().getTime());
            LOGGER.info("Finish individually running test case with data table row: " + individualRun.getCaption());
            for (TeststepRun teststepRun: individualRun.getStepRuns()) {
                if (TestResult.FAILED == teststepRun.getResult()) {
                    individualRun.setResult(TestResult.FAILED);
                    break;
                }
            }
        }

        //  test case run ends
        testcaseRun.setDuration(new Date().getTime() - testcaseRun.getStartTime().getTime());
        LOGGER.info("Finish running test case: " + getTestcase().getName());
        for (TestcaseIndividualRun individualRun: testcaseRun.getIndividualRuns()) {
            if (TestResult.FAILED == individualRun.getResult()) {
                testcaseRun.setResult(TestResult.FAILED);
                break;
            }
        }

        //  persist test case run details into database
        getTestcaseRunDAO().insert(testcaseRun);

        return testcaseRun;
    }
}
