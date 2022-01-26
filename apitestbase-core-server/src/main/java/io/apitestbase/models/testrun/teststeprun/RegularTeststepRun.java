package io.apitestbase.models.testrun.teststeprun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.resources.ResourceJsonViews;

public class RegularTeststepRun extends TeststepRun {
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
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
}
