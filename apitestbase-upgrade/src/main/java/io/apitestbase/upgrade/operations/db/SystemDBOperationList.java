package io.apitestbase.upgrade.operations.db;

import io.apitestbase.upgrade.operations.OperationList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class SystemDBOperationList extends OperationList {
    private void addSimpleSystemDBOperation(String oldVersion, String newVersion, String sqlScriptFileName) {
        getOperationList().add(new SimpleSystemDBOperation(
                new DefaultArtifactVersion(oldVersion), new DefaultArtifactVersion(newVersion),
                "io/apitestbase/upgrade/db/" + sqlScriptFileName));
    }
    public SystemDBOperationList() {

        //  sample code for two operations
//        getOperationList().add(new AdvancedSystemDBOperation_0_18_4_To_0_18_5_Part1());
//        addSimpleSystemDBOperation("0.18.4", "0.18.5","SystemDB_0_18_4_To_0_18_5_Part2.sql");

        addSimpleSystemDBOperation("0.19.0", "0.20.0","SystemDB_0_19_0_To_0_20_0.sql");
    }
}
