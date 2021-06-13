package io.apitestbase.upgrade;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.List;

public class CopyFiles {
    private List<CopyFilesForOneVersionUpgrade> allFiles = new ArrayList();

    public CopyFiles() {
        CopyFilesForOneVersionUpgrade filesForOneVersion = new CopyFilesForOneVersionUpgrade(
                new DefaultArtifactVersion("0.17.1"), new DefaultArtifactVersion("0.18.0"));
        filesForOneVersion.getFilePathMap().put("start.bat", "start.bat");
        filesForOneVersion.getFilePathMap().put("start-team.bat", "start-team.bat");
        allFiles.add(filesForOneVersion);
    }

    public List<CopyFilesForOneVersionUpgrade> getApplicableCopyFiles(DefaultArtifactVersion oldVersion,
                                                                      DefaultArtifactVersion newVersion) {
        List<CopyFilesForOneVersionUpgrade> result = new ArrayList<>();
        for (CopyFilesForOneVersionUpgrade filesForOneVersionUpgrade: allFiles) {
            if (filesForOneVersionUpgrade.getFromVersion().compareTo(oldVersion) >= 0 &&
                    filesForOneVersionUpgrade.getToVersion().compareTo(newVersion) <=0) {
                result.add(filesForOneVersionUpgrade);
            }
        }
        return result;
    }
}
