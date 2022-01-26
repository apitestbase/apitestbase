package io.apitestbase.models.testrun.teststeprun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.testrun.TestRun;
import io.apitestbase.resources.ResourceJsonViews;

public class TeststepIndividualRun extends TestRun {
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    private String caption;      //  caption of the data table row
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    private TeststepAtomicRunResult atomicRunResult = new TeststepAtomicRunResult();

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public TeststepAtomicRunResult getAtomicRunResult() {
        return atomicRunResult;
    }

    public void setAtomicRunResult(TeststepAtomicRunResult atomicRunResult) {
        this.atomicRunResult = atomicRunResult;
    }
}
