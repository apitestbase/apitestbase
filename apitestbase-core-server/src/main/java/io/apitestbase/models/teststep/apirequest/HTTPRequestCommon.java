package io.apitestbase.models.teststep.apirequest;

import io.apitestbase.models.teststep.HTTPHeader;

import java.util.ArrayList;
import java.util.List;

public class HTTPRequestCommon extends APIRequest {
    //  using List instead of Map here to ease the display on ui-grid
    private List<HTTPHeader> headers = new ArrayList<>();

    private String body;

    public List<HTTPHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(List<HTTPHeader> headers) {
        this.headers = headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
