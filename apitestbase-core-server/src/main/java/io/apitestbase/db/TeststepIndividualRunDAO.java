package io.apitestbase.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apitestbase.models.testrun.TeststepIndividualRun;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

@RegisterRowMapper(TeststepIndividualRunMapper.class)
public interface TeststepIndividualRunDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep_individualrun (id IDENTITY PRIMARY KEY, " +
            "teststep_run_id BIGINT NOT NULL, caption VARCHAR(500), " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_run_id) REFERENCES teststep_run(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlQuery("select * from teststep_individualrun where teststep_run_id = :teststepRunId")
    List<TeststepIndividualRun> _findByTeststepRunId(@Bind("teststepRunId") long teststepRunId);

    default List<TeststepIndividualRun> findByTeststepRunId(long teststepRunId) {
        List<TeststepIndividualRun> individualRuns = _findByTeststepRunId(teststepRunId);
        for (TeststepIndividualRun individualRun: individualRuns) {
            individualRun.setAtomicRunResult(
                    teststepAtomicRunResultDAO().findByTeststepIndividualRunId(individualRun.getId()));
        }

        return individualRuns;
    }

    @SqlUpdate("insert into teststep_individualrun (teststep_run_id, caption, starttime, duration, result) values (" +
            ":teststepRunId, :t.caption, :t.startTime, :t.duration, :t.result)")
    @GetGeneratedKeys("id")
    long _insert(@Bind("teststepRunId") long teststepRunId, @BindBean("t") TeststepIndividualRun teststepIndividualRun);

    default void insert(long teststepRunId, TeststepIndividualRun teststepIndividualRun) throws JsonProcessingException {
        long id = _insert(teststepRunId, teststepIndividualRun);
        teststepAtomicRunResultDAO().insert(teststepRunId, id, teststepIndividualRun.getAtomicRunResult());
    }
}