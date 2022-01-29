package io.apitestbase.models.testrun.teststeprun;

public class RegularTeststepRepeatRun extends TeststepRepeatRun {
    private TeststepAtomicRunResult atomicRunResult = new TeststepAtomicRunResult();

    public RegularTeststepRepeatRun(TeststepRepeatRun repeatRun) {
        super.setId(repeatRun.getId());
        super.setResult(repeatRun.getResult());
        super.setStartTime(repeatRun.getStartTime());
        super.setDuration(repeatRun.getDuration());
        super.setIndex(repeatRun.getIndex());
    }

    public RegularTeststepRepeatRun() { }

    public TeststepAtomicRunResult getAtomicRunResult() {
        return atomicRunResult;
    }

    public void setAtomicRunResult(TeststepAtomicRunResult atomicRunResult) {
        this.atomicRunResult = atomicRunResult;
    }
}
