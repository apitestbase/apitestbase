package io.apitestbase.models.teststep;

import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.Properties;
import io.apitestbase.resources.ResourceJsonViews;

@JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.TestcaseExport.class})
public class SFTPTeststepProperties extends Properties {
    private String remoteFilePath;

    public String getRemoteFilePath() {
        return remoteFilePath;
    }

    public void setRemoteFilePath(String remoteFilePath) {
        this.remoteFilePath = remoteFilePath;
    }
}
