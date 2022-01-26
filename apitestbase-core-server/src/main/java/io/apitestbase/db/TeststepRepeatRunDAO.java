package io.apitestbase.db;

import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface TeststepRepeatRunDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep_repeatrun (id IDENTITY PRIMARY KEY, " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "teststep_run_id BIGINT NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_run_id) REFERENCES teststep_run(id) ON DELETE CASCADE)")
    void createTableIfNotExists();
}