package io.apitestbase.models.testrun.testcaserun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.testrun.teststeprun.TeststepRun;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

public class RegularTestcaseRun extends TestcaseRun {
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    private List<TeststepRun> stepRuns = new ArrayList<>();

    public RegularTestcaseRun() {}

    public RegularTestcaseRun(TestcaseRun testcaseRun) {
        super(testcaseRun);
    }

    public List<TeststepRun> getStepRuns() {
        return stepRuns;
    }

    public void setStepRuns(List<TeststepRun> stepRuns) {
        this.stepRuns = stepRuns;
    }
}
