package io.apitestbase.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apitestbase.models.testrun.teststeprun.DataDrivenTeststepRepeatRun;
import io.apitestbase.models.testrun.teststeprun.RegularTeststepRepeatRun;
import io.apitestbase.models.testrun.teststeprun.TeststepIndividualRun;
import io.apitestbase.models.testrun.teststeprun.TeststepRepeatRun;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface TeststepRepeatRunDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep_repeatrun (id IDENTITY PRIMARY KEY, index INTEGER NOT NULL, " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "teststep_run_id BIGINT NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_run_id) REFERENCES teststep_run(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into teststep_repeatrun (index, starttime, duration, result, teststep_run_id) values (" +
            ":t.index, :t.startTime, :t.duration, :t.result, :teststepRunId)")
    @GetGeneratedKeys("id")
    long _insert(@Bind("teststepRunId") long teststepRunId, @BindBean("t") TeststepRepeatRun teststepRepeatRun);

    default void insert(long teststepRunId, TeststepRepeatRun teststepRepeatRun) throws JsonProcessingException {
        long id = _insert(teststepRunId, teststepRepeatRun);

        if (teststepRepeatRun instanceof RegularTeststepRepeatRun) {
            teststepAtomicRunResultDAO().insert(teststepRunId, id, null,
                    ((RegularTeststepRepeatRun) teststepRepeatRun).getAtomicRunResult());
        } else if (teststepRepeatRun instanceof DataDrivenTeststepRepeatRun) {
            DataDrivenTeststepRepeatRun dataDrivenTeststepRepeatRun = (DataDrivenTeststepRepeatRun) teststepRepeatRun;
            for (TeststepIndividualRun teststepIndividualRun: dataDrivenTeststepRepeatRun.getIndividualRuns()) {
                teststepIndividualRunDAO().insert(teststepRunId, id, teststepIndividualRun);
            }
        }
    }
}