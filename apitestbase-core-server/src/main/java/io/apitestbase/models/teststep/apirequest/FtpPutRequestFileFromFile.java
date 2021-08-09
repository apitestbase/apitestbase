package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.teststep.apirequest.FtpPutRequest;
import io.apitestbase.resources.ResourceJsonViews;

public class FtpPutRequestFileFromFile extends FtpPutRequest {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String fileName;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private byte[] fileContent;

    public FtpPutRequestFileFromFile() {}

    public FtpPutRequestFileFromFile(FtpPutRequest ftpPutRequest) {
        setFileFrom(ftpPutRequest.getFileFrom());
        setRemoteFilePath(ftpPutRequest.getRemoteFilePath());
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }
}
