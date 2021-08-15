package io.apitestbase.core.teststep;

import io.apitestbase.models.HTTPMethod;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.apirequest.SOAPRequest;
import io.apitestbase.utils.GeneralUtils;

public class SOAPTeststepRunner extends TeststepRunner {
    public BasicTeststepRun run() throws Exception {
        Teststep teststep = getTeststep();
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        Endpoint endpoint = teststep.getEndpoint();
        SOAPRequest apiRequest = (SOAPRequest) teststep.getApiRequest();
        HTTPAPIResponse apiResponse = GeneralUtils.invokeHTTPAPI(
                endpoint.getUrl(), endpoint.getUsername(), getDecryptedEndpointPassword(),
                HTTPMethod.POST, apiRequest.getHeaders(), apiRequest.getBody());
        basicTeststepRun.setResponse(apiResponse);

        return basicTeststepRun;
    }
}
