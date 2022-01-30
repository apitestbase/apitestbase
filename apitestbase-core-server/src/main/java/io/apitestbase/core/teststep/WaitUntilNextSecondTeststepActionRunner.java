package io.apitestbase.core.teststep;

public class WaitUntilNextSecondTeststepActionRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() throws InterruptedException {
        long startTimeSecond = getTestcaseIndividualRunContext() != null ?
                getTestcaseIndividualRunContext().getTestcaseIndividualRunStartTime().getTime() / 1000 :   //  data driven test case individual run
                getTestcaseRunContext().getTestcaseRunStartTime().getTime() / 1000;              //  regular test case run

        while (System.currentTimeMillis() / 1000 == startTimeSecond) {
            long millisWithinSecond = System.currentTimeMillis() % 1000;
            long millisUntilNextSecond = 1000 - millisWithinSecond;
            Thread.sleep(millisUntilNextSecond);
        }

        return new TeststepActionRunResult();
    }
}
