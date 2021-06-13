package io.apitestbase.upgrade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.apitestbase.Version;
import io.apitestbase.common.Constants;
import io.apitestbase.common.Utils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@CommandLine.Command(name = "upgrade", description = "Upgrade API Test Base")
public class UpgradeCommand implements Runnable {
    @CommandLine.Parameters(description = "Home directory of the API Test Base instance to be upgraded.")
    private String apiTestBaseHome;

    @Override
    public void run() {
        SystemDatabaseYml systemDBConfiguration;
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            ConfigYml configYml = mapper.readValue(new File(apiTestBaseHome, "config.yml"), ConfigYml.class);
            systemDBConfiguration = configYml.getSystemDatabase();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config.yml under " + apiTestBaseHome, e);
        }

        String fullyQualifiedSystemDBURL = getFullyQualifiedSystemDBURL(apiTestBaseHome, systemDBConfiguration.getUrl());
        DefaultArtifactVersion systemDBVersion = getSystemDBVersionStr(fullyQualifiedSystemDBURL, systemDBConfiguration.getUser(),
                systemDBConfiguration.getPassword());
        DefaultArtifactVersion jarFileVersion = new DefaultArtifactVersion(Version.VERSION);

        int comparison = systemDBVersion.compareTo(jarFileVersion);
        if ("SNAPSHOT".equals(systemDBVersion.getQualifier())) {
            System.out.println("System database version " + systemDBVersion + " is a SNAPSHOT version. Upgrade is not supported.");
        } else if ("SNAPSHOT".equals(jarFileVersion.getQualifier())) {
            System.out.println("Jar file version " + jarFileVersion + " is a SNAPSHOT version. Upgrade is not supported.");
        } else if (comparison == 0) {
            System.out.println("System database and the jar file are of the same version, so no need to upgrade.");
        } else if (comparison > 0) {    //  system database version is bigger than the jar file version
            System.out.printf(Constants.PROMPT_TEXT_WHEN_SYSTEM_DB_VERSION_IS_BIGGER_THAN_JAR_VERSION,
                    systemDBVersion, jarFileVersion);
        } else {    //  system database version is smaller than the jar file version
            UpgradeActions upgradeActions = new UpgradeActions();
            try {
                upgradeActions.upgrade(systemDBVersion, jarFileVersion, apiTestBaseHome, fullyQualifiedSystemDBURL,
                        systemDBConfiguration.getUser(), systemDBConfiguration.getPassword());
            } catch (Exception e) {
                throw new RuntimeException("Failed to upgrade API Test Base under " + apiTestBaseHome, e);
            }
        }
    }

    private String getFullyQualifiedSystemDBURL(String apiTestBaseHome, String originalSystemDBURL) {
        String systemDBBaseURL = originalSystemDBURL.split(";")[0];
        String systemDBPath = systemDBBaseURL.replace("jdbc:h2:", "");
        String fullyQualifiedSystemDBPath = systemDBPath;
        if (systemDBPath.startsWith("./")) {
            fullyQualifiedSystemDBPath = Paths.get(apiTestBaseHome, systemDBPath.replace("./", "")).toString();
        }
        String fullyQualifiedSystemDBURL = "jdbc:h2:" + fullyQualifiedSystemDBPath + ";IFEXISTS=TRUE";
        return fullyQualifiedSystemDBURL;
    }

    private DefaultArtifactVersion getSystemDBVersionStr(String fullyQualifiedSystemDBURL, String user, String password) {
        Jdbi jdbi = Jdbi.create(fullyQualifiedSystemDBURL, user, password);
        return Utils.getSystemDBVersion(jdbi);
    }

    public static void main(String[] args) {
        new CommandLine(new UpgradeCommand()).execute(args);
    }
}
