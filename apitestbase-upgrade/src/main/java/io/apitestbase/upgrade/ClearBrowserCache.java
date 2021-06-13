package io.apitestbase.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.HashMap;
import java.util.Map;

public class ClearBrowserCache {
    //  a map of <fromVersion> -> <toVersion>
    private Map<DefaultArtifactVersion, DefaultArtifactVersion> versionMap = new HashMap();

    public ClearBrowserCache() {
        versionMap.put(new DefaultArtifactVersion("0.17.1"), new DefaultArtifactVersion("0.18.0"));
    }

    public Map<DefaultArtifactVersion, DefaultArtifactVersion> getVersionMap() {
        return versionMap;
    }
}
