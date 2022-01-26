package io.apitestbase.upgrade.operations;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ClearBrowserCacheOperationList extends OperationList {
    private void addOperation(String oldVersion, String newVersion) {
        getOperationList().add(new Operation(
                new DefaultArtifactVersion(oldVersion), new DefaultArtifactVersion(newVersion)));
    }
    public ClearBrowserCacheOperationList() {
        addOperation("0.18.3", "0.18.4");
        addOperation("0.18.4", "0.18.5");
        addOperation("0.18.5", "0.18.6");
        addOperation("0.18.7", "0.19.0");
        addOperation("0.19.0", "0.20.0");
    }
}
