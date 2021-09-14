package io.apitestbase.core.teststep;

import io.apitestbase.core.testcase.TestcaseRunContext;
import io.apitestbase.models.teststep.Teststep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TeststepRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeststepRunner.class);

    private Teststep teststep;
    private String decryptedEndpointPassword;
    private TestcaseRunContext testcaseRunContext;    //  set only when running test case

    protected TeststepRunner() {}

    public BasicTeststepRun run() throws Exception {
        LOGGER.info("Start running test step: " + teststep.getName());

        BasicTeststepRun result = _run();

        LOGGER.info("Finish running test step: " + teststep.getName());

        return result;
    }

    protected abstract BasicTeststepRun _run() throws Exception;

    public void setDecryptedEndpointPassword(String decryptedEndpointPassword) {
        this.decryptedEndpointPassword = decryptedEndpointPassword;
    }

    protected void setTeststep(Teststep teststep) {
        this.teststep = teststep;
    }

    protected Teststep getTeststep() {
        return teststep;
    }

    protected String getDecryptedEndpointPassword() {
        return decryptedEndpointPassword;
    }

    protected TestcaseRunContext getTestcaseRunContext() {
        return testcaseRunContext;
    }

    void setTestcaseRunContext(TestcaseRunContext testcaseRunContext) {
        this.testcaseRunContext = testcaseRunContext;
    }
}
