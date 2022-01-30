package io.apitestbase.core.testcase;

import io.apitestbase.APITestBaseConstants;
import io.apitestbase.db.UtilsDAO;
import io.apitestbase.models.DataTable;
import io.apitestbase.models.DataTableCell;
import io.apitestbase.models.DataTableColumn;
import io.apitestbase.models.TestResult;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.testrun.teststeprun.*;
import io.apitestbase.models.teststep.RepeatFixedNumberOfTimesTeststepRunPattern;
import io.apitestbase.models.teststep.RepeatUntilPassTeststepRunPattern;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.TeststepRunPattern;
import io.apitestbase.utils.GeneralUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_DATE_TIME_FORMAT;
import static io.apitestbase.APITestBaseConstants.IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME;

public class TestcaseStepRunner {
    private Logger LOGGER;

    TestcaseStepRunner(Logger LOGGER) {
        this.LOGGER = LOGGER;
    }

    TeststepRun run(Teststep teststep, UtilsDAO utilsDAO, Map<String, String> referenceableStringProperties,
                    Map<String, Endpoint> referenceableEndpointProperties, TestcaseRunContext testcaseRunContext,
                    TestcaseIndividualRunContext testcaseIndividualRunContext) throws IOException, InterruptedException {
        TeststepRun stepRun;

        //  test step run starts
        Date stepRunStartTime = new Date();
        LOGGER.info("Start running test step: " + teststep.getName());
        referenceableStringProperties.put(IMPLICIT_PROPERTY_NAME_TEST_STEP_START_TIME,
                IMPLICIT_PROPERTY_DATE_TIME_FORMAT.format(stepRunStartTime));

        TeststepRunPattern teststepRunPattern = teststep.getRunPattern();
        if (teststepRunPattern == null) {
            stepRun = runTeststep(stepRunStartTime, teststep, utilsDAO,
                    referenceableStringProperties, referenceableEndpointProperties, testcaseRunContext,
                    testcaseIndividualRunContext);
        } else if (teststepRunPattern instanceof RepeatUntilPassTeststepRunPattern) {
            RepeatedTeststepRun repeatedTeststepRun = new RepeatedTeststepRun();
            repeatedTeststepRun.setStartTime(stepRunStartTime);
            RepeatUntilPassTeststepRunPattern repeatUntilPassTeststepRunPattern =
                    (RepeatUntilPassTeststepRunPattern) teststepRunPattern;
            int waitBetweenRepeatRuns = Integer.parseInt(repeatUntilPassTeststepRunPattern.getWaitBetweenRepeatRuns());
            int timeout = Integer.parseInt(repeatUntilPassTeststepRunPattern.getTimeout());
            if (waitBetweenRepeatRuns < 0) {
                throw new IllegalArgumentException("Invalid waitBetweenRepeatRuns value " + waitBetweenRepeatRuns +
                        ". It must be a non-negative integer.");
            }
            if (timeout <= 0) {
                throw new IllegalArgumentException("Invalid timeout value " + timeout + ". It must be a positive integer.");
            }

            LOGGER.info("Start test step repeat run until pass");
            Date timeoutTime = DateUtils.addMilliseconds(stepRunStartTime, timeout);
            TeststepRepeatRun teststepRepeatRun;
            boolean repeatRunPassed;
            int index = 0;
            do {
                index++;
                if (index > 1) {
                    Thread.sleep(waitBetweenRepeatRuns);
                }
                Date repeatRunStartTime = new Date();
                LOGGER.info("Start test step repeat run " + index);
                referenceableStringProperties.put(
                        APITestBaseConstants.IMPLICIT_PROPERTY_NAME_TEST_STEP_REPEAT_RUN_INDEX, String.valueOf(index));
                teststepRepeatRun = runTeststepRepeat(repeatRunStartTime, teststep, utilsDAO,
                        referenceableStringProperties, referenceableEndpointProperties, testcaseRunContext,
                        testcaseIndividualRunContext);
                teststepRepeatRun.setIndex(index);
                addRepeatRun(teststep.getName(), repeatedTeststepRun, teststepRepeatRun);
                repeatRunPassed = teststepRepeatRun.getResult() == TestResult.PASSED;
                LOGGER.info("Finish test step repeat run " + index);
            } while (!repeatRunPassed && System.currentTimeMillis() < timeoutTime.getTime());
            repeatedTeststepRun.setResult(repeatRunPassed ? TestResult.PASSED : TestResult.FAILED);

            stepRun = repeatedTeststepRun;

            LOGGER.info("Finish test step repeat run until pass");
        } else if (teststepRunPattern instanceof RepeatFixedNumberOfTimesTeststepRunPattern) {
            RepeatedTeststepRun repeatedTeststepRun = new RepeatedTeststepRun();
            repeatedTeststepRun.setStartTime(stepRunStartTime);
            RepeatFixedNumberOfTimesTeststepRunPattern repeatFixedNumberOfTimesTeststepRunPattern =
                    (RepeatFixedNumberOfTimesTeststepRunPattern) teststepRunPattern;
            int repeatTimes = Integer.parseInt(repeatFixedNumberOfTimesTeststepRunPattern.getRepeatTimes());
            if (repeatTimes <= 0) {
                throw new IllegalArgumentException("Invalid repeatTimes value " + repeatTimes + ". It must be a positive integer.");
            }

            LOGGER.info("Start test step repeat run fixed number of times");
            repeatedTeststepRun.setResult(TestResult.PASSED);
            for (int i = 1; i < repeatTimes; i++) {
                Date repeatRunStartTime = new Date();
                referenceableStringProperties.put(
                        APITestBaseConstants.IMPLICIT_PROPERTY_NAME_TEST_STEP_REPEAT_RUN_INDEX, String.valueOf(i));
                TeststepRepeatRun teststepRepeatRun = runTeststepRepeat(repeatRunStartTime, teststep, utilsDAO,
                        referenceableStringProperties, referenceableEndpointProperties, testcaseRunContext,
                        testcaseIndividualRunContext);
                teststepRepeatRun.setIndex(i);
                addRepeatRun(teststep.getName(), repeatedTeststepRun, teststepRepeatRun);
                if (teststepRepeatRun.getResult() == TestResult.FAILED) {
                    repeatedTeststepRun.setResult(TestResult.FAILED);
                }
            }

            stepRun = repeatedTeststepRun;
            LOGGER.info("Finish test step repeat run fixed number of times");
        } else {
            throw new IllegalArgumentException("Unsupported test step run pattern");
        }

        //  test step run ends
        stepRun.setDuration(new Date().getTime() - stepRun.getStartTime().getTime());
        LOGGER.info("Finish running test step: " + teststep.getName());

        return stepRun;
    }

    private void addRepeatRun(String stepName, RepeatedTeststepRun repeatedTeststepRun, TeststepRepeatRun repeatRun) {
        if (repeatedTeststepRun.getRepeatRuns().size() >= RepeatedTeststepRun.REPEAT_CAP) {
            throw new IllegalStateException("Teststep '" + stepName + "' is repeating more than " +
                    RepeatedTeststepRun.REPEAT_CAP + " times.");
        } else {
            repeatedTeststepRun.getRepeatRuns().add(repeatRun);
        }
    }

    private TeststepRun runTeststep(Date startTime, Teststep teststep, UtilsDAO utilsDAO,
                                Map<String, String> referenceableStringProperties,
                                Map<String, Endpoint> referenceableEndpointProperties,
                                TestcaseRunContext testcaseRunContext,
                                TestcaseIndividualRunContext testcaseIndividualRunContext) throws IOException {
        DataTable stepDataTable = teststep.getDataTable();
        if (stepDataTable == null || stepDataTable.getRows().isEmpty()) {
            RegularTeststepRun regularTeststepRun = new RegularTeststepRun();
            regularTeststepRun.setStartTime(startTime);
            TestResult result = new AtomicTeststepRunner().run(LOGGER, regularTeststepRun.getAtomicRunResult(), teststep,
                    utilsDAO, referenceableStringProperties, referenceableEndpointProperties, testcaseRunContext,
                    testcaseIndividualRunContext);
            regularTeststepRun.setResult(result);
            return regularTeststepRun;
        } else {
            GeneralUtils.checkDuplicatePropertyNames(referenceableStringProperties.keySet(),
                    stepDataTable.getNonCaptionColumnNames());
            return runDataDrivenTeststep(startTime, teststep, utilsDAO, referenceableStringProperties,
                    referenceableEndpointProperties, testcaseRunContext, testcaseIndividualRunContext);
        }
    }

    private TeststepRepeatRun runTeststepRepeat(Date startTime, Teststep teststep, UtilsDAO utilsDAO,
                                    Map<String, String> referenceableStringProperties,
                                    Map<String, Endpoint> referenceableEndpointProperties,
                                    TestcaseRunContext testcaseRunContext,
                                    TestcaseIndividualRunContext testcaseIndividualRunContext) throws IOException {
        DataTable stepDataTable = teststep.getDataTable();
        if (stepDataTable == null || stepDataTable.getRows().isEmpty()) {
            RegularTeststepRepeatRun regularTeststepRepeatRun = new RegularTeststepRepeatRun();
            regularTeststepRepeatRun.setStartTime(startTime);
            TestResult result = new AtomicTeststepRunner().run(LOGGER, regularTeststepRepeatRun.getAtomicRunResult(),
                    teststep, utilsDAO, referenceableStringProperties, referenceableEndpointProperties,
                    testcaseRunContext, testcaseIndividualRunContext);
            regularTeststepRepeatRun.setDuration(new Date().getTime() - startTime.getTime());
            regularTeststepRepeatRun.setResult(result);
            return regularTeststepRepeatRun;
        } else {
            GeneralUtils.checkDuplicatePropertyNames(referenceableStringProperties.keySet(),
                    stepDataTable.getNonCaptionColumnNames());
            return runDataDrivenTeststepRepeat(startTime, teststep, utilsDAO, referenceableStringProperties,
                    referenceableEndpointProperties, testcaseRunContext, testcaseIndividualRunContext);
        }
    }

    private DataDrivenTeststepRun runDataDrivenTeststep(Date stepRunStartTime, Teststep teststep,
                                                        UtilsDAO utilsDAO,
                                                        Map<String, String> referenceableStringProperties,
                                                        Map<String, Endpoint> referenceableEndpointProperties,
                                                        TestcaseRunContext testcaseRunContext,
                                                        TestcaseIndividualRunContext testcaseIndividualRunContext) throws IOException {
        DataDrivenTeststepRun stepRun = new DataDrivenTeststepRun();
        stepRun.setResult(TestResult.PASSED);
        stepRun.setStartTime(stepRunStartTime);

        runDataDrivenTeststepIndividuals(teststep.getDataTable(), stepRun.getIndividualRuns(), teststep, utilsDAO,
                referenceableStringProperties, referenceableEndpointProperties, testcaseRunContext, testcaseIndividualRunContext);

        for (TeststepIndividualRun individualRun: stepRun.getIndividualRuns()) {
            if (TestResult.FAILED == individualRun.getResult()) {
                stepRun.setResult(TestResult.FAILED);
                break;
            }
        }

        return stepRun;
    }

    private DataDrivenTeststepRepeatRun runDataDrivenTeststepRepeat(Date startTime, Teststep teststep,
                                                        UtilsDAO utilsDAO,
                                                        Map<String, String> referenceableStringProperties,
                                                        Map<String, Endpoint> referenceableEndpointProperties,
                                                        TestcaseRunContext testcaseRunContext,
                                                        TestcaseIndividualRunContext testcaseIndividualRunContext) throws IOException {
        DataDrivenTeststepRepeatRun stepRepeatRun = new DataDrivenTeststepRepeatRun();
        stepRepeatRun.setResult(TestResult.PASSED);
        stepRepeatRun.setStartTime(startTime);

        runDataDrivenTeststepIndividuals(teststep.getDataTable(), stepRepeatRun.getIndividualRuns(), teststep, utilsDAO,
                referenceableStringProperties, referenceableEndpointProperties, testcaseRunContext, testcaseIndividualRunContext);

        stepRepeatRun.setDuration(new Date().getTime() - startTime.getTime());
        for (TeststepIndividualRun individualRun: stepRepeatRun.getIndividualRuns()) {
            if (TestResult.FAILED == individualRun.getResult()) {
                stepRepeatRun.setResult(TestResult.FAILED);
                break;
            }
        }

        return stepRepeatRun;
    }

    private void runDataDrivenTeststepIndividuals(DataTable dataTable, List<TeststepIndividualRun> individualRuns,
                                                  Teststep teststep,
                                                  UtilsDAO utilsDAO,
                                                  Map<String, String> referenceableStringProperties,
                                                  Map<String, Endpoint> referenceableEndpointProperties,
                                                  TestcaseRunContext testcaseRunContext,
                                                  TestcaseIndividualRunContext testcaseIndividualRunContext) throws IOException {
        for (int dataTableRowIndex = 0; dataTableRowIndex < dataTable.getRows().size(); dataTableRowIndex++) {
            LinkedHashMap<String, DataTableCell> dataTableRow = dataTable.getRows().get(dataTableRowIndex);
            TeststepIndividualRun individualRun = new TeststepIndividualRun();
            individualRuns.add(individualRun);

            //  test step individual run starts
            individualRun.setStartTime(new Date());
            individualRun.setCaption(dataTableRow.get(DataTableColumn.COLUMN_NAME_CAPTION).getValue());
            LOGGER.info("Start individually running test step with data table row: " + individualRun.getCaption());
            referenceableStringProperties.putAll(dataTable.getStringPropertiesInRow(dataTableRowIndex));

            individualRun.setResult(new AtomicTeststepRunner().run(LOGGER, individualRun.getAtomicRunResult(), teststep,
                    utilsDAO, referenceableStringProperties, referenceableEndpointProperties, testcaseRunContext,
                    testcaseIndividualRunContext));

            //  test step individual run ends
            individualRun.setDuration(new Date().getTime() - individualRun.getStartTime().getTime());
            LOGGER.info("Finish individually running test step with data table row: " + individualRun.getCaption());
        }
    }
}
