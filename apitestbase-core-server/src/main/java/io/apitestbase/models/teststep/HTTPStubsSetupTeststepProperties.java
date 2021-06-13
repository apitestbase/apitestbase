package io.apitestbase.models.teststep;

import io.apitestbase.models.HTTPStubMapping;
import io.apitestbase.models.Properties;

import java.util.List;

public class HTTPStubsSetupTeststepProperties extends Properties {
    private List<HTTPStubMapping> httpStubMappings;

    public List<HTTPStubMapping> getHttpStubMappings() {
        return httpStubMappings;
    }

    public void setHttpStubMappings(List<HTTPStubMapping> httpStubMappings) {
        this.httpStubMappings = httpStubMappings;
    }
}
