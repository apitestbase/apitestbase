package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TeststepRunPatternType {
    NONE("None"), REPEAT_UNTIL_PASS("RepeatUntilPass"),
    REPEAT_FIXED_NUMBER_OF_TIMES("RepeatFixedNumberOfTimes");

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
}
