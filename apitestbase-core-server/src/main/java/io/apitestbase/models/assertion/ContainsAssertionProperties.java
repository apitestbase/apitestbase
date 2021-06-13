package io.apitestbase.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class ContainsAssertionProperties extends Properties {
    private String contains;

    public ContainsAssertionProperties() {}

    public ContainsAssertionProperties(String contains) {
        this.contains = contains;
    }

    public String getContains() {
        return contains;
    }

    public void setContains(String contains) {
        this.contains = contains;
    }
}
