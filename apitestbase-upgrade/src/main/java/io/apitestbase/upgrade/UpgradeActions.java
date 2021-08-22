package io.apitestbase.upgrade;

import io.apitestbase.upgrade.operations.*;
import io.apitestbase.upgrade.operations.db.SystemDBOperation;
import io.apitestbase.upgrade.operations.db.SystemDBOperationList;
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.jdbi.v3.core.Jdbi;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class UpgradeActions {
    private static final Logger LOGGER = Logger.getLogger("Upgrade");

    protected void upgrade(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion,
                           String apiTestBaseHome, String fullyQualifiedSystemDBURL, String user, String password)
            throws Exception {
        Formatter logFormatter = new LogFormatter();
        LOGGER.getParent().getHandlers()[0].setFormatter(logFormatter);    //  set formatter for console logging
        LOGGER.info("Upgrading API Test Base from v" + systemDatabaseVersion + " to v" + jarFileVersion + ".");

        //  set up temp upgrade directory
        Path upgradeWorkspace = Files.createTempDirectory("apitestbase-upgrade-");
        Path logFilePath = Paths.get(upgradeWorkspace.toString(),
                "upgrade-from-v" + systemDatabaseVersion + "-to-v" + jarFileVersion + ".log");
        FileHandler logFileHandler = new FileHandler(logFilePath.toString());
        logFileHandler.setFormatter(logFormatter);
        LOGGER.addHandler(logFileHandler);
        LOGGER.info("Created temp upgrade directory " + upgradeWorkspace.toString());
        Path oldFolderInTempUpgradeDir = Paths.get(upgradeWorkspace.toString(), "old");
        Path newFolderInTempUpgradeDir = Paths.get(upgradeWorkspace.toString(), "new");
        Files.createDirectory(oldFolderInTempUpgradeDir);
        Files.createDirectory(newFolderInTempUpgradeDir);

        //  system DB upgrade includes schema change and/or data migration
        boolean needsSystemDBUpgrade = upgradeSystemDBInTempDirIfNeeded(systemDatabaseVersion, jarFileVersion, apiTestBaseHome,
                fullyQualifiedSystemDBURL, user, password, oldFolderInTempUpgradeDir, newFolderInTempUpgradeDir);

        boolean clearBrowserCacheNeeded = clearBrowserCacheIfNeeded(systemDatabaseVersion, jarFileVersion);

        //  ------------------------- below steps will modify files in <APITestBase_Home> -------------------------

        copyFilesToBeUpgraded(apiTestBaseHome, systemDatabaseVersion, jarFileVersion);

        deleteObsoleteFiles(apiTestBaseHome, systemDatabaseVersion, jarFileVersion);

        deleteOldJarsFromAPITestBaseHome(apiTestBaseHome);

        copyNewJarFromDistToAPITestBaseHome(jarFileVersion, apiTestBaseHome);

        //  request user to execute pre system database change (upgrade, or simply version update) general manual upgrades if needed
        preSystemDBChangeGeneralManualUpgrades(systemDatabaseVersion, jarFileVersion);

        if (needsSystemDBUpgrade) {            //  copy files from the temp 'new' folder to <APITestBase_Home>
            String systemDBFileName = getSystemDBFileName(fullyQualifiedSystemDBURL);
            Path apiTestBaseHomeSystemDatabaseFolder = Paths.get(apiTestBaseHome, "database");
            Path sourceFilePath = Paths.get(newFolderInTempUpgradeDir.toString(), "database", systemDBFileName);
            Path targetFilePath = Paths.get(apiTestBaseHomeSystemDatabaseFolder.toString(), systemDBFileName);
            Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Copied " + sourceFilePath + " to " + targetFilePath + ".");
        } else {    //  only update version of system database under <APITestBase_Home>
            Jdbi jdbi = Jdbi.create(fullyQualifiedSystemDBURL, user, password);
            updateVersionTableInSystemDatabase(jdbi, fullyQualifiedSystemDBURL, jarFileVersion);
        }

        String lineDelimiter = "------------------------------------------------------------------------";
        LOGGER.info(lineDelimiter);
        LOGGER.info("UPGRADE SUCCESS");
        LOGGER.info(lineDelimiter);
        LOGGER.info("You can start API Test Base now.");
        if (clearBrowserCacheNeeded) {
            LOGGER.info("If API Test Base page is already open, refresh the page (no need to restart browser).");
        }
        LOGGER.info(lineDelimiter);
        LOGGER.info("Refer to " + logFilePath + " for upgrade logs.");
    }

    private boolean upgradeSystemDBInTempDirIfNeeded(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion,
                                            String apiTestBaseHome, String fullyQualifiedSystemDBURL, String user, String password,
                                            Path oldFolderInTempUpgradeDir, Path newFolderInTempUpgradeDir) throws IOException {
        boolean needsSystemDBUpgrade = new SystemDBOperationList()
                .hasAtLeastOneApplicableOperation(systemDatabaseVersion, jarFileVersion);
        if (needsSystemDBUpgrade) {
            LOGGER.info("Please manually backup <APITestBase_Home>/database folder to your normal maintenance backup location. To confirm backup completion, type y and then Enter.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
            LOGGER.info("User confirmed system database backup completion.");

            upgradeSystemDBInTempDir(apiTestBaseHome, fullyQualifiedSystemDBURL, user, password,
                    new SystemDBOperationList().getApplicableOperations(systemDatabaseVersion, jarFileVersion),
                    oldFolderInTempUpgradeDir, newFolderInTempUpgradeDir, jarFileVersion);

            return true;
        } else {
            return false;
        }
    }

    private boolean clearBrowserCacheIfNeeded(DefaultArtifactVersion oldVersion, DefaultArtifactVersion newVersion) {
        boolean clearBrowserCacheNeeded =
                new ClearBrowserCacheOperationList().hasAtLeastOneApplicableOperation(oldVersion, newVersion);
        if (clearBrowserCacheNeeded) {
            LOGGER.info("Please clear browser cached images and files (last hour is enough). To confirm clear completion, type y and then Enter.");
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
            LOGGER.info("User confirmed browser cache clear completion.");
        }

        return clearBrowserCacheNeeded;
    }

    private void copyNewJarFromDistToAPITestBaseHome(DefaultArtifactVersion newJarFileVersion, String apiTestBaseHome)
            throws IOException {
        String newJarFileName = "apitestbase-" + newJarFileVersion + ".jar";
        Path soureFilePath = Paths.get(".", newJarFileName).toAbsolutePath();
        Path targetFilePath = Paths.get(apiTestBaseHome, newJarFileName).toAbsolutePath();
        Files.copy(soureFilePath, targetFilePath);
        LOGGER.info("Copied " + soureFilePath + " to " + targetFilePath + ".");
    }

    private void deleteOldJarsFromAPITestBaseHome(String apiTestBaseHome) throws IOException {
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(
                Paths.get(apiTestBaseHome), "apitestbase-*.jar")) {
            dirStream.forEach(filePath -> {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                LOGGER.info("Deleted " + filePath + ".");
            });
        }
    }

    private void copyFilesToBeUpgraded(String apiTestBaseHome, DefaultArtifactVersion oldVersion,
                                       DefaultArtifactVersion newVersion) throws IOException {
        List<Operation> applicableCopyFiles =
                new CopyFilesOperationList().getApplicableOperations(oldVersion, newVersion);
        for (Operation filesForOneVersionUpgrade: applicableCopyFiles) {
            CopyFilesOperation copyFilesOperation =
                    (CopyFilesOperation) filesForOneVersionUpgrade;
            Map<String, String> filePathMap = copyFilesOperation.getFilePathMap();
            for (Map.Entry<String, String> mapEntry: filePathMap.entrySet()) {
                Path sourceFilePath = Paths.get(".", mapEntry.getKey()).toAbsolutePath();
                Path targetFilePath = Paths.get(apiTestBaseHome, mapEntry.getValue()).toAbsolutePath();
                Files.copy(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
                LOGGER.info("Copied " + sourceFilePath + " to " + targetFilePath + ".");
            }
        }
    }

    private void deleteObsoleteFiles(String apiTestBaseHome, DefaultArtifactVersion oldVersion,
                                       DefaultArtifactVersion newVersion) throws IOException {
        List<Operation> applicableDeleteFiles =
                new DeleteFilesOperationList().getApplicableOperations(oldVersion, newVersion);
        for (Operation filesForOneVersionUpgrade: applicableDeleteFiles) {
            DeleteFilesOperation deleteFilesOperation =
                    (DeleteFilesOperation) filesForOneVersionUpgrade;
            List<String> filePathList = deleteFilesOperation.getFilePathList();
            for (String filePath: filePathList) {
                Path path = Paths.get(apiTestBaseHome, filePath).toAbsolutePath();
                if (Files.exists(path)) {
                    if (Files.isDirectory(path)) {
                        FileUtils.deleteDirectory(path.toFile());
                    } else {
                        Files.delete(path);
                    }
                    LOGGER.info("Deleted " + path + ".");
                }
            }
        }
    }

    /**
     * Result is sorted by fromVersion.
     * @param oldVersion
     * @param newVersion
     * @param subPackage
     * @param prefix
     * @param extension
     * @return
     */
    private List<ResourceFile> getApplicableUpgradeResourceFiles(DefaultArtifactVersion oldVersion,
                                                                 DefaultArtifactVersion newVersion, String subPackage,
                                                                 String prefix, String extension) {
        List<ResourceFile> result = new ArrayList<>();

        Reflections reflections = new Reflections(
                getClass().getPackage().getName() + "." + subPackage, new ResourcesScanner());
        Set<String> upgradeFilePaths =
                reflections.getResources(Pattern.compile(prefix + ".*\\." + extension));

        for (String upgradeFilePath: upgradeFilePaths) {
            String[] upgradeFilePathFragments = upgradeFilePath.split("/");
            String upgradeFileName = upgradeFilePathFragments[upgradeFilePathFragments.length - 1];
            String[] versionsInUpgradeFileName = upgradeFileName.replace(prefix + "_", "").
                    replace("." + extension, "").split("_To_");
            DefaultArtifactVersion fromVersionInUpgradeFileName = new DefaultArtifactVersion(
                    versionsInUpgradeFileName[0].replace("_", "."));
            DefaultArtifactVersion toVersionInUpgradeFileName = new DefaultArtifactVersion(
                    versionsInUpgradeFileName[1].replace("_", "."));
            if (fromVersionInUpgradeFileName.compareTo(oldVersion) >= 0 &&
                    toVersionInUpgradeFileName.compareTo(newVersion) <=0) {
                ResourceFile upgradeResourceFile = new ResourceFile(
                        fromVersionInUpgradeFileName, toVersionInUpgradeFileName, upgradeFilePath);
                result.add(upgradeResourceFile);
            }
        }

        Collections.sort(result);

        return result;
    }

    private String getSystemDBFileName(String fullyQualifiedSystemDBURL) {
        String systemDBBaseURL = fullyQualifiedSystemDBURL.split(";")[0];

        //  copy system database to the old and new folders under the temp workspace
        String systemDBPath = systemDBBaseURL.replace("jdbc:h2:", "");
        String[] systemDBFileRelativePathFragments = systemDBPath.split("[/\\\\]");  // split by / and \
        String systemDBFileName = systemDBFileRelativePathFragments[systemDBFileRelativePathFragments.length - 1] + ".mv.db";
        return systemDBFileName;
    }

    private void upgradeSystemDBInTempDir(String apiTestBaseHome, String fullyQualifiedSystemDBURL,
                                          String user, String password,
                                          List<Operation> applicableSystemDBOperations, Path oldDir, Path newDir,
                                          DefaultArtifactVersion jarFileVersion)
            throws IOException {
        Path oldDatabaseFolder = Files.createDirectory(Paths.get(oldDir.toString(), "database"));
        Path newDatabaseFolder = Files.createDirectory(Paths.get(newDir.toString(), "database"));
        String systemDBFileName = getSystemDBFileName(fullyQualifiedSystemDBURL);

        Path sourceFile = Paths.get(apiTestBaseHome, "database", systemDBFileName);
        Path targetOldFile = Paths.get(oldDatabaseFolder.toString(), systemDBFileName);
        Path targetNewFile = Paths.get(newDatabaseFolder.toString(), systemDBFileName);
        Files.copy(sourceFile, targetOldFile);
        LOGGER.info("Copied current system database to " + oldDatabaseFolder);
        Files.copy(sourceFile, targetNewFile);
        LOGGER.info("Copied current system database to " + newDatabaseFolder);

        String newSystemDBURL = "jdbc:h2:" + targetNewFile.toString().replace(".mv.db", "") + ";IFEXISTS=TRUE";
        Jdbi jdbi = Jdbi.create(newSystemDBURL, user, password);

        //  run System DB operations against the system database in the 'new' folder
        for (Operation operation: applicableSystemDBOperations) {
            SystemDBOperation systemDBOperation =  (SystemDBOperation) operation;
            systemDBOperation.run(jdbi, newSystemDBURL);
        }

        updateVersionTableInSystemDatabase(jdbi, newSystemDBURL, jarFileVersion);
    }

    private void updateVersionTableInSystemDatabase(Jdbi jdbi, String systemDBURL, DefaultArtifactVersion newVersion) {
        jdbi.withHandle(handle -> handle
                .createUpdate("update version set version = ?, updated = CURRENT_TIMESTAMP")
                .bind(0, newVersion.toString())
                .execute());
        LOGGER.info("Updated Version to " + newVersion + " in " + systemDBURL + ".");
    }

    private void preSystemDBChangeGeneralManualUpgrades(DefaultArtifactVersion systemDatabaseVersion, DefaultArtifactVersion jarFileVersion) throws IOException {
        List<ResourceFile> applicableGeneralManualUpgrades =
                getApplicableUpgradeResourceFiles(systemDatabaseVersion, jarFileVersion, "manual", "GeneralPreSystemDBChange", "txt");
        for (ResourceFile manualStep: applicableGeneralManualUpgrades) {
            LOGGER.info(GeneralUtils.getResourceAsText(manualStep.getResourcePath()));   //  display manual step details to user
            Scanner scanner = new Scanner(System.in);
            String line = null;
            while (!"y".equalsIgnoreCase(line)) {
                line = scanner.nextLine().trim();
            }
            LOGGER.info("User confirmed manual step completion.");
        }
    }
}
