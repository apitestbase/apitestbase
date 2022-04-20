package io.apitestbase.upgrade.operations;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ClearBrowserCacheOperationList extends OperationList {
    private void addOperation(String oldVersion, String newVersion) {
        getOperationList().add(new Operation(
                new DefaultArtifactVersion(oldVersion), new DefaultArtifactVersion(newVersion)));
    }
    public ClearBrowserCacheOperationList() {
        addOperation("0.19.0", "0.20.0");
    }
}
