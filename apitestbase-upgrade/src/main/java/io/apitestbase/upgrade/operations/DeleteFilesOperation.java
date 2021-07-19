package io.apitestbase.upgrade.operations;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.List;

public class DeleteFilesOperation extends Operation {
    private List<String> filePathList = new ArrayList<>();

    public DeleteFilesOperation(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion) {
        super(fromVersion, toVersion);
    }

    public List<String> getFilePathList() {
        return filePathList;
    }
}
