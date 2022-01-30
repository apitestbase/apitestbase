package io.apitestbase.core.teststep;

import io.apitestbase.models.teststep.WaitTeststepProperties;

public class WaitTeststepRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() throws InterruptedException {
        WaitTeststepProperties teststepProperties = (WaitTeststepProperties) getTeststep().getOtherProperties();
        long milliseconds = Long.valueOf(teststepProperties.getMilliseconds());
        if (milliseconds > 0) {
            Thread.sleep(milliseconds);
        }
        return new TeststepActionRunResult();
    }
}