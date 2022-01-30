package io.apitestbase.models.testrun.teststeprun;

import io.apitestbase.models.teststep.Teststep;

public class RegularTeststepRun extends TeststepRun {
    private TeststepAtomicRunResult atomicRunResult = new TeststepAtomicRunResult();

    public RegularTeststepRun(TeststepRun stepRun) {
        super.setId(stepRun.getId());
        super.setResult(stepRun.getResult());
        super.setStartTime(stepRun.getStartTime());
        super.setDuration(stepRun.getDuration());
    }

    public RegularTeststepRun() {}

    public TeststepAtomicRunResult getAtomicRunResult() {
        return atomicRunResult;
    }

    public void setAtomicRunResult(TeststepAtomicRunResult atomicRunResult) {
        this.atomicRunResult = atomicRunResult;
    }

    @Override
    public Teststep getMetaTeststep() {
        return atomicRunResult.getTeststep();
    }
}
