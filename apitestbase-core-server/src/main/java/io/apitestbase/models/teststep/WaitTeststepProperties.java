package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class WaitTeststepProperties extends Properties {
    private String milliseconds;  //  String instead of long to allow API Test Base property to be used in this field

    public WaitTeststepProperties() {}

    public WaitTeststepProperties(String milliseconds) {
        this.milliseconds = milliseconds;
    }

    public String getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(String milliseconds) {
        this.milliseconds = milliseconds;
    }
}
