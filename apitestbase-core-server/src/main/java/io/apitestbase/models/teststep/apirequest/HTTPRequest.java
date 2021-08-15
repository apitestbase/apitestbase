package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.HTTPMethod;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class HTTPRequest extends HTTPRequestCommon {
    private HTTPMethod method;

    public HTTPMethod getMethod() {
        return method;
    }

    public void setMethod(HTTPMethod method) {
        this.method = method;
    }
}
