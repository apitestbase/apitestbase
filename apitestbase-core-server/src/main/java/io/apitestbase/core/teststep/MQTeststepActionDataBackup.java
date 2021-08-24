package io.apitestbase.core.teststep;

import io.apitestbase.models.teststep.apirequest.MQEnqueueOrPublishFromFileRequest;
import io.apitestbase.models.teststep.apirequest.MQEnqueueOrPublishFromTextRequest;

/**
 * Only for MQ test step.
 */
public class MQTeststepActionDataBackup {
    private MQEnqueueOrPublishFromTextRequest textRequest;
    private MQEnqueueOrPublishFromFileRequest fileRequest;

    public MQEnqueueOrPublishFromTextRequest getTextRequest() {
        return textRequest;
    }

    public void setTextRequest(MQEnqueueOrPublishFromTextRequest textRequest) {
        this.textRequest = textRequest;
    }

    public MQEnqueueOrPublishFromFileRequest getFileRequest() {
        return fileRequest;
    }

    public void setFileRequest(MQEnqueueOrPublishFromFileRequest fileRequest) {
        this.fileRequest = fileRequest;
    }
}
