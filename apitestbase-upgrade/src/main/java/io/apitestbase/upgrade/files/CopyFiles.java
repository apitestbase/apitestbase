package io.apitestbase.upgrade.files;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class CopyFiles extends FilesOperations {
    public CopyFiles() {
        CopyFilesForOneVersionUpgrade filesForOneVersion = new CopyFilesForOneVersionUpgrade(
                new DefaultArtifactVersion("0.17.1"), new DefaultArtifactVersion("0.18.0"));
        filesForOneVersion.getFilePathMap().put("start.bat", "start.bat");
        filesForOneVersion.getFilePathMap().put("start-team.bat", "start-team.bat");
        getFilesOperations().add(filesForOneVersion);
    }
}
