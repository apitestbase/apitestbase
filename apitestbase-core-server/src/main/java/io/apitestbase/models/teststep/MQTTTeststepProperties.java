package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class MQTTTeststepProperties extends Properties {
    private String topicString;

    public String getTopicString() {
        return topicString;
    }

    public void setTopicString(String topicString) {
        this.topicString = topicString;
    }
}
