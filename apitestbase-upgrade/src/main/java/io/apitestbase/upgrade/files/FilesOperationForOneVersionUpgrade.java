package io.apitestbase.upgrade.files;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class FilesOperationForOneVersionUpgrade {
    private DefaultArtifactVersion fromVersion;
    private DefaultArtifactVersion toVersion;

    public FilesOperationForOneVersionUpgrade(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion) {
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
