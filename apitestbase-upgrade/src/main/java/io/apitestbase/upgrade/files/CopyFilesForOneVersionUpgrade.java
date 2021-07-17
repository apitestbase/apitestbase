package io.apitestbase.upgrade.files;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.HashMap;
import java.util.Map;

public class CopyFilesForOneVersionUpgrade extends FilesOperationForOneVersionUpgrade {
    private Map<String, String> filePathMap = new HashMap<>();

    public CopyFilesForOneVersionUpgrade(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion) {
        super(fromVersion, toVersion);
    }

    public Map<String, String> getFilePathMap() {
        return filePathMap;
    }
}
