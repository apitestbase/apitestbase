package io.apitestbase.upgrade.operations;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ClearBrowserCacheOperationList extends OperationList {
    public ClearBrowserCacheOperationList() {
        getOperationList().add(new Operation(new DefaultArtifactVersion("0.18.3"), new DefaultArtifactVersion("0.18.4")));
        getOperationList().add(new Operation(new DefaultArtifactVersion("0.18.4"), new DefaultArtifactVersion("0.18.5")));
        getOperationList().add(new Operation(new DefaultArtifactVersion("0.18.5"), new DefaultArtifactVersion("0.18.6")));
    }
}
