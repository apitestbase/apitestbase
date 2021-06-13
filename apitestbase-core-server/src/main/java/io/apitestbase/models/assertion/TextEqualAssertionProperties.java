package io.apitestbase.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class TextEqualAssertionProperties extends Properties {
    private String expectedText;

    public String getExpectedText() {
        return expectedText;
    }

    public void setExpectedText(String expectedText) {
        this.expectedText = expectedText;
    }
}
