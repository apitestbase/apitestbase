package io.apitestbase.core.testcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.apitestbase.models.endpoint.Endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Used for passing information across test steps when running a test case.
 */
public class TestcaseRunContext {
    private Date testcaseRunStartTime;
    private WireMockServer wireMockServer;            //  the universal WireMock server inside the API Test Base instance
    private Map<Short, UUID> httpStubMappingInstanceIds = new HashMap<>();  //  mapping from stub mapping number to stub mapping instance UUID (after loaded into mock server)
    private Map<String, String> referenceableStringProperties = new HashMap<>();
    private Map<String, Endpoint> referenceableEndpointProperties = new HashMap<>();

    public Date getTestcaseRunStartTime() {
        return testcaseRunStartTime;
    }

    public void setTestcaseRunStartTime(Date testcaseRunStartTime) {
        this.testcaseRunStartTime = testcaseRunStartTime;
    }

    public WireMockServer getWireMockServer() {
        return wireMockServer;
    }

    public void setWireMockServer(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    public Map<Short, UUID> getHttpStubMappingInstanceIds() {
        return httpStubMappingInstanceIds;
    }

    public Map<String, String> getReferenceableStringProperties() {
        return referenceableStringProperties;
    }

    public Map<String, Endpoint> getReferenceableEndpointProperties() {
        return referenceableEndpointProperties;
    }
}
