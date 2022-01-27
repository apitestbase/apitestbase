package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TeststepRunPatternType {
    REPEAT_UNTIL_PASS(Constants.REPEAT_UNTIL_PASS),
    REPEAT_FIXED_NUMBER_OF_TIMES(Constants.REPEAT_FIXED_NUMBER_OF_TIMES);

    private final String text;

    TeststepRunPatternType(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static TeststepRunPatternType getByText(String text) {
        for (TeststepRunPatternType e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }

    public static class Constants {
        static final String REPEAT_UNTIL_PASS = "RepeatUntilPass";
        static final String REPEAT_FIXED_NUMBER_OF_TIMES = "RepeatFixedNumberOfTimes";
    }
}
