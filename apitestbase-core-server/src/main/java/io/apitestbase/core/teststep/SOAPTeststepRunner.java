package io.apitestbase.core.teststep;

import io.apitestbase.models.HTTPMethod;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.SOAPTeststepProperties;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.utils.IronTestUtils;

public class SOAPTeststepRunner extends TeststepRunner {
    public BasicTeststepRun run() throws Exception {
        Teststep teststep = getTeststep();
        BasicTeststepRun basicTeststepRun = new BasicTeststepRun();
        Endpoint endpoint = teststep.getEndpoint();
        SOAPTeststepProperties otherProperties = (SOAPTeststepProperties) teststep.getOtherProperties();
        HTTPAPIResponse apiResponse = IronTestUtils.invokeHTTPAPI(
                endpoint.getUrl(), endpoint.getUsername(), getDecryptedEndpointPassword(),
                HTTPMethod.POST, otherProperties.getHttpHeaders(), (String) teststep.getRequest());
        basicTeststepRun.setResponse(apiResponse);

        return basicTeststepRun;
    }
}
