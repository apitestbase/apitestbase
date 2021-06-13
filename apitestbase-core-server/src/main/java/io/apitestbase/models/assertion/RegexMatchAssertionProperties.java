package io.apitestbase.models.assertion;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class RegexMatchAssertionProperties extends Properties {
    private String regex;

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }
}
