package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JMSMessagePropertyType {
    STRING("String"), BOOLEAN("Boolean"), SHORT("Short"), INTEGER("Integer"),
    LONG("Long"), FLOAT("Float"), DOUBLE("Double");

    private final String text;

    JMSMessagePropertyType(String text) {
        this.text = text;
    }

    @Override
    @JsonValue
    public String toString() {
        return text;
    }

    public static JMSMessagePropertyType getByText(String text) {
        for (JMSMessagePropertyType e : values()) {
            if (e.text.equals(text)) {
                return e;
            }
        }
        return null;
    }
}
