package io.apitestbase.models.testrun.teststeprun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

public class RepeatedTeststepRun extends TeststepRun {
    @JsonView(ResourceJsonViews.TestcaseRunOutlineOnTestcaseEditView.class)
    private List<TeststepRepeatRun> repeatRuns = new ArrayList<>();

    public RepeatedTeststepRun(TeststepRun stepRun) {
        super.setId(stepRun.getId());
        super.setResult(stepRun.getResult());
        super.setStartTime(stepRun.getStartTime());
        super.setDuration(stepRun.getDuration());
    }

    public RepeatedTeststepRun() { }

    public List<TeststepRepeatRun> getRepeatRuns() {
        return repeatRuns;
    }

    public void setRepeatRuns(List<TeststepRepeatRun> repeatRuns) {
        this.repeatRuns = repeatRuns;
    }

    @Override
    public Teststep getMetaTeststep() {
        return repeatRuns.get(0).getMetaTeststep();
    }
}
