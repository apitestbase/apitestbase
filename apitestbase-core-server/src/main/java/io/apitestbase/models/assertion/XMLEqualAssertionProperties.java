package io.apitestbase.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class XMLEqualAssertionProperties extends Properties {
    private String expectedXML;

    public String getExpectedXML() {
        return expectedXML;
    }

    public void setExpectedXML(String expectedXML) {
        this.expectedXML = expectedXML;
    }
}
