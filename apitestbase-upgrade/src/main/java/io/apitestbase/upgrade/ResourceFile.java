package io.apitestbase.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class ResourceFile implements Comparable<ResourceFile> {
    private DefaultArtifactVersion fromVersion;
    private DefaultArtifactVersion toVersion;
    private String resourcePath;

    public ResourceFile(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion, String resourcePath) {
        this.fromVersion = fromVersion;
        this.toVersion = toVersion;
        this.resourcePath = resourcePath;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public int compareTo(ResourceFile o) {
        return this.fromVersion.compareTo(o.fromVersion);
    }
}
