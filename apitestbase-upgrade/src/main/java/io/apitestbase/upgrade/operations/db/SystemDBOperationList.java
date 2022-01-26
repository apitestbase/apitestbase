package io.apitestbase.upgrade.operations.db;

import io.apitestbase.upgrade.operations.OperationList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public class SystemDBOperationList extends OperationList {
    public SystemDBOperationList() {
        getOperationList().add(new SimpleSystemDBOperation(
                new DefaultArtifactVersion("0.18.3"), new DefaultArtifactVersion("0.18.4"),
                "io/apitestbase/upgrade/db/SystemDB_0_18_3_To_0_18_4.sql"));

        //  two operations for 0.18.4 to 0.18.5
        getOperationList().add(new AdvancedSystemDBOperation_0_18_4_To_0_18_5_Part1());
        getOperationList().add(
                new SimpleSystemDBOperation(new DefaultArtifactVersion("0.18.4"), new DefaultArtifactVersion("0.18.5"),
                "io/apitestbase/upgrade/db/SystemDB_0_18_4_To_0_18_5_Part2.sql"));

        getOperationList().add(new SimpleSystemDBOperation(
                new DefaultArtifactVersion("0.18.5"), new DefaultArtifactVersion("0.18.6"),
                "io/apitestbase/upgrade/db/SystemDB_0_18_5_To_0_18_6.sql"));

        getOperationList().add(new SimpleSystemDBOperation(
                new DefaultArtifactVersion("0.18.7"), new DefaultArtifactVersion("0.19.0"),
                "io/apitestbase/upgrade/db/SystemDB_0_18_7_To_0_19_0.sql"));

        getOperationList().add(new SimpleSystemDBOperation(
                new DefaultArtifactVersion("0.19.0"), new DefaultArtifactVersion("0.20.0"),
                "io/apitestbase/upgrade/db/SystemDB_0_19_0_To_0_20_0.sql"));
    }
}
