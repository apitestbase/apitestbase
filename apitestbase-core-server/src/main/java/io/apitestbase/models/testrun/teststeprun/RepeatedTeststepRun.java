package io.apitestbase.models.testrun.teststeprun;

import java.util.ArrayList;
import java.util.List;

public class RepeatedTeststepRun extends TeststepRun {
    private List<TeststepRepeatRun> repeatRuns = new ArrayList<>();

    public List<TeststepRepeatRun> getRepeatRuns() {
        return repeatRuns;
    }
}
