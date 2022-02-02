package io.apitestbase.core.teststep;

import io.apitestbase.models.teststep.JMSMessageProperty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JMSBrowseQueueResponse extends APIResponse {
    private int browseMessageIndex;
    private Map<String, String> header = new LinkedHashMap();
    private List<JMSMessageProperty> properties = new ArrayList<>();
    private String body;

    public int getBrowseMessageIndex() {
        return browseMessageIndex;
    }

    public void setBrowseMessageIndex(int browseMessageIndex) {
        this.browseMessageIndex = browseMessageIndex;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public List<JMSMessageProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<JMSMessageProperty> properties) {
        this.properties = properties;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
