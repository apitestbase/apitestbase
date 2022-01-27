package io.apitestbase.models.teststep;

public class RepeatUntilPassTeststepRunPattern extends TeststepRunPattern {
    private String intervalBetweenRepeatRuns = "1000";

    public String getIntervalBetweenRepeatRuns() {
        return intervalBetweenRepeatRuns;
    }

    public void setIntervalBetweenRepeatRuns(String intervalBetweenRepeatRuns) {
        this.intervalBetweenRepeatRuns = intervalBetweenRepeatRuns;
    }
}
