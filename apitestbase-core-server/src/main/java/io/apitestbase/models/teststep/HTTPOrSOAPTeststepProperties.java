package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.APITestBaseConstants;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class HTTPOrSOAPTeststepProperties extends Properties {
    //  request timeout in seconds; String instead of long to allow API Test Base property to be used in this field
    private String timeout = APITestBaseConstants.HTTP_REQUEST_DEFAULT_TIMEOUT_IN_SECONDS;

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }
}
