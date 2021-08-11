package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.teststep.JMSMessageProperty;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.ArrayList;
import java.util.List;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class JMSRequest extends APIRequest {
    private String body;

    //  using List instead of Map here to ease the display on ui-grid
    private List<JMSMessageProperty> properties = new ArrayList<>();

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<JMSMessageProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<JMSMessageProperty> properties) {
        this.properties = properties;
    }
}
