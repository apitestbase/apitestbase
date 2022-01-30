package io.apitestbase.core.teststep;

import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.HTTPOrSOAPTeststepProperties;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.apirequest.HTTPRequest;
import io.apitestbase.utils.GeneralUtils;

public class HTTPTeststepRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() throws Exception {
        Teststep teststep = getTeststep();
        TeststepActionRunResult basicTeststepRun = new TeststepActionRunResult();
        HTTPOrSOAPTeststepProperties otherProperties = (HTTPOrSOAPTeststepProperties) teststep.getOtherProperties();
        Endpoint endpoint = teststep.getEndpoint();
        HTTPRequest apiRequest = (HTTPRequest) teststep.getApiRequest();
        HTTPAPIResponse apiResponse = GeneralUtils.invokeHTTPAPI(
                endpoint.getUrl(), endpoint.getUsername(), getDecryptedEndpointPassword(),
                apiRequest.getMethod(), apiRequest.getHeaders(), apiRequest.getBody(), otherProperties.getTimeout());
        basicTeststepRun.setResponse(apiResponse);

        return basicTeststepRun;
    }
}
