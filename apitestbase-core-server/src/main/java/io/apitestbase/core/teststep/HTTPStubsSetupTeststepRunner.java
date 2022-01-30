package io.apitestbase.core.teststep;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import io.apitestbase.models.HTTPStubMapping;
import io.apitestbase.models.teststep.HTTPStubsSetupTeststepProperties;
import io.apitestbase.utils.GeneralUtils;

import java.util.Map;
import java.util.UUID;

public class HTTPStubsSetupTeststepRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() {
        WireMockServer wireMockServer = getTestcaseRunContext().getWireMockServer();

        //  reset mock server
        wireMockServer.resetAll();

        //  load stub mappings into mock server
        Map<Short, UUID> httpStubMappingInstanceIds = getTestcaseRunContext().getHttpStubMappingInstanceIds();
        HTTPStubsSetupTeststepProperties otherProperties = (HTTPStubsSetupTeststepProperties) getTeststep().getOtherProperties();
        wireMockServer.loadMappingsUsing(stubMappings -> {
            for (HTTPStubMapping stubMapping: otherProperties.getHttpStubMappings()) {
                StubMapping stubInstance = GeneralUtils.createStubInstance(stubMapping.getId(), stubMapping.getNumber(), stubMapping.getSpec());
                stubMappings.addMapping(stubInstance);
                httpStubMappingInstanceIds.put(stubMapping.getNumber(), stubInstance.getId());
            }
        });

        return new TeststepActionRunResult();
    }
}
