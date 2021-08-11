package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.teststep.apirequest.JMSMessagePropertyType;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class JMSMessageProperty {
    private String name;
    private String value;
    private JMSMessagePropertyType type = JMSMessagePropertyType.STRING;

    public JMSMessageProperty() {}

    public JMSMessageProperty(String name, String value, JMSMessagePropertyType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public JMSMessagePropertyType getType() {
        return type;
    }

    public void setType(JMSMessagePropertyType type) {
        this.type = type;
    }
}
