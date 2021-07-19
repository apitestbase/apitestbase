package io.apitestbase.upgrade.operations;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

/**
 * Operation for one version upgrade.
 */
public class Operation {
    private DefaultArtifactVersion fromVersion;
    private DefaultArtifactVersion toVersion;

    public Operation(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
    }

    public DefaultArtifactVersion getFromVersion() {
        return fromVersion;
    }

    public DefaultArtifactVersion getToVersion() {
        return toVersion;
    }
}
