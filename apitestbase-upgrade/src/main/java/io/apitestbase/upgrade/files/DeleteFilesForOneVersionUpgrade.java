package io.apitestbase.upgrade.files;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.List;

public class DeleteFilesForOneVersionUpgrade extends FilesOperationForOneVersionUpgrade {
    private List<String> filePathList = new ArrayList<>();

    public DeleteFilesForOneVersionUpgrade(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion) {
        super(fromVersion, toVersion);
    }

    public List<String> getFilePathList() {
        return filePathList;
    }
}
