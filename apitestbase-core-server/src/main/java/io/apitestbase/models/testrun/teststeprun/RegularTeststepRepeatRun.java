package io.apitestbase.models.testrun.teststeprun;

public class RegularTeststepRepeatRun extends TeststepRepeatRun {
    private TeststepAtomicRunResult atomicRunResult = new TeststepAtomicRunResult();

    public TeststepAtomicRunResult getAtomicRunResult() {
        return atomicRunResult;
    }
}
