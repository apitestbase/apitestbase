package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RepeatUntilPassTeststepRunPattern.class,
                name = TeststepRunPatternType.Constants.REPEAT_UNTIL_PASS),
        @JsonSubTypes.Type(value = RepeatFixedNumberOfTimesTeststepRunPattern.class,
                name = TeststepRunPatternType.Constants.REPEAT_FIXED_NUMBER_OF_TIMES)
})
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
