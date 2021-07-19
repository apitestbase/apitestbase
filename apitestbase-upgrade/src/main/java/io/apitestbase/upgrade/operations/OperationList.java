package io.apitestbase.upgrade.operations;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.util.ArrayList;
import java.util.List;

public class OperationList {
    private List<Operation> operationList = new ArrayList();

    protected List<Operation> getOperationList() {
        return operationList;
    }

    public boolean hasAtLeastOneApplicableOperation(DefaultArtifactVersion oldVersion,
                                                   DefaultArtifactVersion newVersion) {
        for (Operation filesForOneVersionUpgrade: operationList) {
            if (filesForOneVersionUpgrade.getFromVersion().compareTo(oldVersion) >= 0 &&
                    filesForOneVersionUpgrade.getToVersion().compareTo(newVersion) <=0) {
                return true;
            }
        }
        return false;
    }

    public List<Operation> getApplicableOperations(DefaultArtifactVersion oldVersion,
                                                   DefaultArtifactVersion newVersion) {
        List<Operation> result = new ArrayList<>();
        for (Operation filesForOneVersionUpgrade: operationList) {
            if (filesForOneVersionUpgrade.getFromVersion().compareTo(oldVersion) >= 0 &&
                    filesForOneVersionUpgrade.getToVersion().compareTo(newVersion) <=0) {
                result.add(filesForOneVersionUpgrade);
            }
        }
        return result;
    }
}
