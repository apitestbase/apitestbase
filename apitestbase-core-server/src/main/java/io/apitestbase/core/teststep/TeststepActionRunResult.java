package io.apitestbase.core.teststep;

/**
 * Result of test step invocation or action run.
 */
public class TeststepActionRunResult {
    private APIResponse response;            //  API response (could be null when there is no endpoint, no API invocation, or API invocation response is not used)
    private String infoMessage;         //  some additional information when the test step finishes running successfully

    public APIResponse getResponse() {
        return response;
    }

    public void setResponse(APIResponse response) {
        this.response = response;
    }

    public String getInfoMessage() {
        return infoMessage;
    }

    public void setInfoMessage(String infoMessage) {
        this.infoMessage = infoMessage;
    }
}
