package io.apitestbase.upgrade.operations.db;

import io.apitestbase.upgrade.operations.Operation;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;

public abstract class SystemDBOperation extends Operation {
    public SystemDBOperation(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion) {
        super(fromVersion, toVersion);
    }

    public abstract void run(Jdbi jdbi, String newSystemDBURL) throws IOException;
}
