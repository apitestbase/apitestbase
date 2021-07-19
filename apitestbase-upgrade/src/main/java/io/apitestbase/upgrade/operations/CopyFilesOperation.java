package io.apitestbase.upgrade.operations;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.HashMap;
import java.util.Map;

public class CopyFilesOperation extends Operation {
    private Map<String, String> filePathMap = new HashMap<>();

    public CopyFilesOperation(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion) {
        super(fromVersion, toVersion);
    }

    public Map<String, String> getFilePathMap() {
        return filePathMap;
    }
}
