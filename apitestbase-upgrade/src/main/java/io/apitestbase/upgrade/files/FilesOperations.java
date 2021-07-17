package io.apitestbase.upgrade.files;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.List;

public class FilesOperations {
    private List<FilesOperationForOneVersionUpgrade> filesOperations = new ArrayList();

    public List<FilesOperationForOneVersionUpgrade> getFilesOperations() {
        return filesOperations;
    }

    public List<FilesOperationForOneVersionUpgrade> getApplicableFilesOperations(DefaultArtifactVersion oldVersion,
                                                                                 DefaultArtifactVersion newVersion) {
        List<FilesOperationForOneVersionUpgrade> result = new ArrayList<>();
        for (FilesOperationForOneVersionUpgrade filesForOneVersionUpgrade: filesOperations) {
            if (filesForOneVersionUpgrade.getFromVersion().compareTo(oldVersion) >= 0 &&
                    filesForOneVersionUpgrade.getToVersion().compareTo(newVersion) <=0) {
                result.add(filesForOneVersionUpgrade);
            }
        }
        return result;
    }
}
