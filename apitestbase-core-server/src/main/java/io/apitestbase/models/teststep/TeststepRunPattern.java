package io.apitestbase.models.teststep;

public class TeststepRunPattern {
    private TeststepRunPatternType type;

    public TeststepRunPattern() {}

    public TeststepRunPattern(TeststepRunPatternType type) {
        this.type = type;
    }

    public TeststepRunPatternType getType() {
        return type;
    }

    public void setPatternType(TeststepRunPatternType type) {
        this.type = type;
    }
}
