package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class MQTTRequest extends APIRequest {
    private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
