package io.apitestbase.models.testrun.testcaserun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.testrun.TestRun;
import io.apitestbase.models.testrun.teststeprun.TeststepRun;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

/**
 * An individual run of a data driven test case, corresponding to one row in the data table.
 */
public class TestcaseIndividualRun extends TestRun {
    @JsonView(ResourceJsonViews.TestcaseRunOutlineOnTestcaseEditView.class)
    private String caption;      //  caption of the data table row
    @JsonView(ResourceJsonViews.TestcaseRunOutlineOnTestcaseEditView.class)
    private List<TeststepRun> stepRuns = new ArrayList<>();

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public List<TeststepRun> getStepRuns() {
        return stepRuns;
    }

    public void setStepRuns(List<TeststepRun> stepRuns) {
        this.stepRuns = stepRuns;
    }
}
