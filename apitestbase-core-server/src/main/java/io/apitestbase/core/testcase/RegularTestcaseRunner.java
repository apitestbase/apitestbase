package io.apitestbase.core.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.apitestbase.db.TestcaseRunDAO;
import io.apitestbase.db.UtilsDAO;
import io.apitestbase.models.TestResult;
import io.apitestbase.models.Testcase;
import io.apitestbase.models.testrun.RegularTestcaseRun;
import io.apitestbase.models.testrun.TestcaseRun;
import io.apitestbase.models.testrun.TeststepRun;
import io.apitestbase.models.teststep.Teststep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

public class RegularTestcaseRunner extends TestcaseRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegularTestcaseRunner.class);

    public RegularTestcaseRunner(Testcase testcase, UtilsDAO utilsDAO, TestcaseRunDAO testcaseRunDAO, WireMockServer wireMockServer) {
        super(testcase, utilsDAO, testcaseRunDAO, LOGGER, wireMockServer);
    }

    @Override
    public TestcaseRun run() throws IOException {
        RegularTestcaseRun testcaseRun = new RegularTestcaseRun();

        preProcessing();
        startTestcaseRun(testcaseRun);

        //  run test steps
        for (Teststep teststep : getTestcase().getTeststeps()) {
            testcaseRun.getStepRuns().add(runTeststep(teststep, null));
        }

        //  test case run ends
        testcaseRun.setDuration(new Date().getTime() - testcaseRun.getStartTime().getTime());
        LOGGER.info("Finish running test case: " + getTestcase().getName());
        for (TeststepRun teststepRun: testcaseRun.getStepRuns()) {
            if (TestResult.FAILED == teststepRun.getResult()) {
                testcaseRun.setResult(TestResult.FAILED);
                break;
            }
        }

        //  persist test case run details into database
        getTestcaseRunDAO().insert(testcaseRun);

        return testcaseRun;
    }
}
