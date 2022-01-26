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
        addSimpleSystemDBOperation("0.18.3", "0.18.4","SystemDB_0_18_3_To_0_18_4.sql");

        //  two operations for 0.18.4 to 0.18.5
        getOperationList().add(new AdvancedSystemDBOperation_0_18_4_To_0_18_5_Part1());
        addSimpleSystemDBOperation("0.18.4", "0.18.5","SystemDB_0_18_4_To_0_18_5_Part2.sql");

        addSimpleSystemDBOperation("0.18.5", "0.18.6","SystemDB_0_18_5_To_0_18_6.sql");
        addSimpleSystemDBOperation("0.18.7", "0.19.0","SystemDB_0_18_7_To_0_19_0.sql");
        addSimpleSystemDBOperation("0.19.0", "0.20.0","SystemDB_0_19_0_To_0_20_0.sql");
    }
}
