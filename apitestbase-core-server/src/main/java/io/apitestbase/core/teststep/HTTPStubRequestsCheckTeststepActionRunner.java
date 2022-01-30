package io.apitestbase.core.teststep;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import io.apitestbase.utils.GeneralUtils;

import java.util.List;

public class HTTPStubRequestsCheckTeststepActionRunner extends TeststepActionRunner {
    @Override
    public TeststepActionRunResult run() {
        TeststepActionRunResult basicTeststepRun = new TeststepActionRunResult();

        WireMockServer wireMockServer = getTestcaseRunContext().getWireMockServer();
        WireMockServerAPIResponse response = new WireMockServerAPIResponse();

        List<ServeEvent> allServeEvents = wireMockServer.getAllServeEvents();
        for (ServeEvent serveEvent: allServeEvents) {
            response.getAllServeEvents().add(GeneralUtils.updateUnmatchedStubRequest(serveEvent, wireMockServer));
        }

        basicTeststepRun.setResponse(response);

        return basicTeststepRun;
    }
}
