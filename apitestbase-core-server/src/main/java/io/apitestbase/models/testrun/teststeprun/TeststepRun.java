package io.apitestbase.models.testrun.teststeprun;

import io.apitestbase.models.testrun.TestRun;
import io.apitestbase.models.teststep.Teststep;

/**
 * Used for test case running.
 */
public class TeststepRun extends TestRun {
    /**
     * To be overridden by subclasses
     * @return
     */
    public Teststep getMetaTeststep() {
        return null;
    }
}
