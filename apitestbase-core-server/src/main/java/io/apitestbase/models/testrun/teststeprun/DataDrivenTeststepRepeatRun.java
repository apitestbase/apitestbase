package io.apitestbase.models.testrun.teststeprun;

import java.util.ArrayList;
import java.util.List;

public class DataDrivenTeststepRepeatRun extends TeststepRepeatRun {
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
}
