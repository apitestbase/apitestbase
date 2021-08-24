package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.teststep.MQRFH2Header;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class MQEnqueueOrPublishFromTextRequest extends MQRequest {
    private String body;
    private MQRFH2Header rfh2Header;    // null means no RFH2 header

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public MQRFH2Header getRfh2Header() {
        return rfh2Header;
    }

    public void setRfh2Header(MQRFH2Header rfh2Header) {
        this.rfh2Header = rfh2Header;
    }

    @Override
    public void clear() {
        this.body = null;
        this.rfh2Header = null;
    }
}
