package io.apitestbase.core.teststep;

import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.apirequest.HTTPRequest;
import io.apitestbase.utils.GeneralUtils;

public class HTTPTeststepRunner extends TeststepRunner {
    public BasicTeststepRun run() throws Exception {
        Teststep teststep = getTeststep();
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        Endpoint endpoint = teststep.getEndpoint();
        HTTPRequest apiRequest = (HTTPRequest) teststep.getApiRequest();
        HTTPAPIResponse apiResponse = GeneralUtils.invokeHTTPAPI(
                endpoint.getUrl(), endpoint.getUsername(), getDecryptedEndpointPassword(),
                apiRequest.getMethod(), apiRequest.getHeaders(), apiRequest.getBody());
        basicTeststepRun.setResponse(apiResponse);

        return basicTeststepRun;
    }
}
