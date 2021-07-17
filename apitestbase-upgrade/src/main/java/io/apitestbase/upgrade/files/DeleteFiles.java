package io.apitestbase.upgrade.files;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class DeleteFiles extends FilesOperations {
    public DeleteFiles() {
        DeleteFilesForOneVersionUpgrade filesForOneVersion = new DeleteFilesForOneVersionUpgrade(
                new DefaultArtifactVersion("0.18.1"), new DefaultArtifactVersion("0.18.2"));
        filesForOneVersion.getFilePathList().add("lib/iib/v90/");
        getFilesOperations().add(filesForOneVersion);
    }
}
