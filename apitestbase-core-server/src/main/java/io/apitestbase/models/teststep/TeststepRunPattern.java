package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
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
