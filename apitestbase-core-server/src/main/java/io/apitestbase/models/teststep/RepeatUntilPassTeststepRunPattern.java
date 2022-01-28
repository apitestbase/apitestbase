package io.apitestbase.models.teststep;

public class RepeatUntilPassTeststepRunPattern extends TeststepRunPattern {
    private String waitBetweenRepeatRuns = "1000";
    private String timeout = "15000";

    public String getWaitBetweenRepeatRuns() {
        return waitBetweenRepeatRuns;
    }

    public void setWaitBetweenRepeatRuns(String waitBetweenRepeatRuns) {
        this.waitBetweenRepeatRuns = waitBetweenRepeatRuns;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
