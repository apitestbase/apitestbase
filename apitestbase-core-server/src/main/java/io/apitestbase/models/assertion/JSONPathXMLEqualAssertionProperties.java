package io.apitestbase.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class JSONPathXMLEqualAssertionProperties extends Properties {
    private String jsonPath;
    private String expectedXML;

    public JSONPathXMLEqualAssertionProperties() {}

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getExpectedXML() {
        return expectedXML;
    }

    public void setExpectedXML(String expectedXML) {
        this.expectedXML = expectedXML;
    }
}
