package io.apitestbase.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class JSONEqualAssertionProperties extends Properties {
    private String expectedJSON;

    public String getExpectedJSON() {
        return expectedJSON;
    }

    public void setExpectedJSON(String expectedJSON) {
        this.expectedJSON = expectedJSON;
    }
}
