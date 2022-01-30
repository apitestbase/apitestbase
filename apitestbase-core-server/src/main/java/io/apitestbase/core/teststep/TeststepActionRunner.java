package io.apitestbase.core.teststep;

import io.apitestbase.core.testcase.TestcaseIndividualRunContext;
import io.apitestbase.core.testcase.TestcaseRunContext;
import io.apitestbase.models.teststep.Teststep;

/**
 * Class for running test step invocation or action.
 */
public abstract class TeststepActionRunner {
    private Teststep teststep;
    private String decryptedEndpointPassword;
    private TestcaseRunContext testcaseRunContext;    //  set when running test case
    private TestcaseIndividualRunContext testcaseIndividualRunContext;    //  set when running data driven test case

    protected TeststepActionRunner() {}

    public abstract TeststepActionRunResult run() throws Exception;

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

    public TestcaseIndividualRunContext getTestcaseIndividualRunContext() {
        return testcaseIndividualRunContext;
    }

    public void setTestcaseIndividualRunContext(TestcaseIndividualRunContext testcaseIndividualRunContext) {
        this.testcaseIndividualRunContext = testcaseIndividualRunContext;
    }
}
