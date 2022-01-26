package io.apitestbase.models.testrun.teststeprun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

public class DataDrivenTeststepRun extends TeststepRun {
    @JsonView(ResourceJsonViews.TestcaseRunResultOnTestcaseEditView.class)
    private List<TeststepIndividualRun> individualRuns = new ArrayList<>();

    public DataDrivenTeststepRun(TeststepRun stepRun) {
        super.setId(stepRun.getId());
        super.setResult(stepRun.getResult());
        super.setStartTime(stepRun.getStartTime());
        super.setDuration(stepRun.getDuration());
    }

    public DataDrivenTeststepRun() {}

    public List<TeststepIndividualRun> getIndividualRuns() {
        return individualRuns;
    }

    public void setIndividualRuns(List<TeststepIndividualRun> individualRuns) {
        this.individualRuns = individualRuns;
    }
}
