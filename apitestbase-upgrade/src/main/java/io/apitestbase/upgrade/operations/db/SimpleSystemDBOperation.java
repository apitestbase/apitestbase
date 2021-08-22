package io.apitestbase.upgrade.operations.db;

import io.apitestbase.upgrade.GeneralUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Run a SQL script
 */
public class SimpleSystemDBOperation extends SystemDBOperation {
    private static final Logger LOGGER = Logger.getLogger("Upgrade");
    private String sqlScriptPath;

    public SimpleSystemDBOperation(DefaultArtifactVersion fromVersion, DefaultArtifactVersion toVersion,
                                   String sqlScriptPath) {
        super(fromVersion, toVersion);
        this.sqlScriptPath = sqlScriptPath;
    }

    @Override
    public void run(Jdbi jdbi, String newSystemDBURL) throws IOException {
        String sqlScript = GeneralUtils.getResourceAsText(sqlScriptPath);
        jdbi.withHandle(handle -> handle.createScript(sqlScript).execute());
        LOGGER.info("Executed SQL script " + sqlScriptPath + " in " + newSystemDBURL + ".");
    }
}
