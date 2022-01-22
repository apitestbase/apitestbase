package io.apitestbase.core.testcase;

import io.apitestbase.models.endpoint.Endpoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestcaseIndividualRunContext {
    private Date testcaseIndividualRunStartTime;
    private Map<String, String> referenceableStringProperties = new HashMap<>();
    private Map<String, Endpoint> referenceableEndpointProperties = new HashMap<>();

    public Date getTestcaseIndividualRunStartTime() {
        return testcaseIndividualRunStartTime;
    }

    public void setTestcaseIndividualRunStartTime(Date testcaseIndividualRunStartTime) {
        this.testcaseIndividualRunStartTime = testcaseIndividualRunStartTime;
    }

    public Map<String, String> getReferenceableStringProperties() {
        return referenceableStringProperties;
    }

    public Map<String, Endpoint> getReferenceableEndpointProperties() {
        return referenceableEndpointProperties;
    }
}

