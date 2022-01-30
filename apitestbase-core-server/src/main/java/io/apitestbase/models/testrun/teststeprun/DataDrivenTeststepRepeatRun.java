package io.apitestbase.models.testrun.teststeprun;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

public class DataDrivenTeststepRepeatRun extends TeststepRepeatRun {
    @JsonView(ResourceJsonViews.TestcaseRunOutlineOnTestcaseEditView.class)
    private List<TeststepIndividualRun> individualRuns = new ArrayList<>();

    public DataDrivenTeststepRepeatRun(TeststepRepeatRun repeatRun) {
        super.setId(repeatRun.getId());
        super.setResult(repeatRun.getResult());
        super.setStartTime(repeatRun.getStartTime());
        super.setDuration(repeatRun.getDuration());
        super.setIndex(repeatRun.getIndex());
    }

    public DataDrivenTeststepRepeatRun() { }

    public List<TeststepIndividualRun> getIndividualRuns() {
        return individualRuns;
    }

    public void setIndividualRuns(List<TeststepIndividualRun> individualRuns) {
        this.individualRuns = individualRuns;
    }

    @Override
    public Teststep getMetaTeststep() {
        return individualRuns.get(0).getAtomicRunResult().getTeststep();
    }
}
