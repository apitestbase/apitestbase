package io.apitestbase.models.teststep;

public class RepeatFixedNumberOfTimesTeststepRunPattern extends TeststepRunPattern {
    private String repeatTimes = "1";

    public String getRepeatTimes() {
        return repeatTimes;
    }

    public void setRepeatTimes(String repeatTimes) {
        this.repeatTimes = repeatTimes;
    }
}
