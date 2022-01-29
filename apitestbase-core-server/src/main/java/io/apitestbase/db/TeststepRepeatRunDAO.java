package io.apitestbase.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apitestbase.models.testrun.teststeprun.DataDrivenTeststepRepeatRun;
import io.apitestbase.models.testrun.teststeprun.RegularTeststepRepeatRun;
import io.apitestbase.models.testrun.teststeprun.TeststepIndividualRun;
import io.apitestbase.models.testrun.teststeprun.TeststepRepeatRun;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.ListIterator;

@RegisterRowMapper(TeststepRepeatRunMapper.class)
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

    @SqlQuery("select * from teststep_repeatrun where teststep_run_id = :teststepRunId")
    List<TeststepRepeatRun> _findByTeststepRunId(@Bind("teststepRunId") long teststepRunId);

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

    default List<TeststepRepeatRun> findByTeststepRunId(long teststepRunId) {
        List<TeststepRepeatRun> repeatRuns = _findByTeststepRunId(teststepRunId);

        ListIterator<TeststepRepeatRun> repeatRunsIterator = repeatRuns.listIterator();
        while (repeatRunsIterator.hasNext()) {
            TeststepRepeatRun repeatRun = repeatRunsIterator.next();
            repeatRunsIterator.set(resolveTeststepRepeatRun(repeatRun));
        }

        return repeatRuns;
    }

    default TeststepRepeatRun resolveTeststepRepeatRun(TeststepRepeatRun repeatRun) {
        List<TeststepIndividualRun> individualRuns = teststepIndividualRunDAO().findByTeststepRepeatRunId(repeatRun.getId());
        if (individualRuns.size() > 0) {  //  it is a data driven test step repeat run
            DataDrivenTeststepRepeatRun dataDrivenTeststepRepeatRun = new DataDrivenTeststepRepeatRun(repeatRun);
            dataDrivenTeststepRepeatRun.setIndividualRuns(individualRuns);
            return dataDrivenTeststepRepeatRun;
        } else {                          //  it is a regular test step repeat run
            RegularTeststepRepeatRun regularTeststepRepeatRun = new RegularTeststepRepeatRun(repeatRun);
            regularTeststepRepeatRun.setAtomicRunResult(
                    teststepAtomicRunResultDAO().findFirstByTeststepRepeatRunId(repeatRun.getId()));
            return regularTeststepRepeatRun;
        }
    }
}