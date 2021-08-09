package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.resources.ResourceJsonViews;

public class FtpPutRequest extends APIRequest {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private FtpPutFileFrom fileFrom = FtpPutFileFrom.TEXT;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String remoteFilePath;

    public FtpPutFileFrom getFileFrom() {
        return fileFrom;
    }

    public void setFileFrom(FtpPutFileFrom fileFrom) {
        this.fileFrom = fileFrom;
    }

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }
}
