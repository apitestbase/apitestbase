package io.apitestbase.models.testrun.teststeprun;

import io.apitestbase.models.testrun.TestRun;
import io.apitestbase.models.teststep.Teststep;

public class TeststepRepeatRun extends TestRun {
    private int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * To be overridden by subclasses
     * @return
     */
    public Teststep getMetaTeststep() {
        return null;
    }
}
