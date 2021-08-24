package io.apitestbase.models.teststep.apirequest;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.resources.ResourceJsonViews;

public class MQEnqueueOrPublishFromFileRequest extends MQRequest implements APIRequestFile {
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
    private String fileName;
    @JsonView(ResourceJsonViews.TestcaseExport.class)
    private byte[] fileContent;

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

    @Override
    public void clear() {
        this.fileName = null;
        this.fileContent = null;
    }
}
