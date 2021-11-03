package io.apitestbase.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.apitestbase.models.testrun.DataDrivenTeststepRun;
import io.apitestbase.models.testrun.RegularTeststepRun;
import io.apitestbase.models.testrun.TeststepIndividualRun;
import io.apitestbase.models.testrun.TeststepRun;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.ListIterator;

@RegisterRowMapper(TeststepRunMapper.class)
public interface TeststepRunDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep_run (id IDENTITY PRIMARY KEY, " +
            "testcase_run_id BIGINT NOT NULL, testcase_individualrun_id BIGINT, " +
            "starttime TIMESTAMP NOT NULL, duration BIGINT NOT NULL, result varchar(15) NOT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_run_id) REFERENCES testcase_run(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (testcase_individualrun_id) REFERENCES testcase_individualrun(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into teststep_run (testcase_run_id, testcase_individualrun_id, starttime, duration, " +
            "result) values (:testcaseRunId, :testcaseIndividualRunId, :t.startTime, :t.duration, :t.result)")
    @GetGeneratedKeys("id")
    long _insert(@Bind("testcaseRunId") long testcaseRunId,
                 @Bind("testcaseIndividualRunId") Long testcaseIndividualRunId, @BindBean("t") TeststepRun teststepRun);

    default void insert(long testcaseRunId, Long testcaseIndividualRunId, TeststepRun teststepRun)
            throws JsonProcessingException {
        long id = _insert(testcaseRunId, testcaseIndividualRunId, teststepRun);

        if (teststepRun instanceof RegularTeststepRun) {
            teststepAtomicRunResultDAO().insert(id, null,
                    ((RegularTeststepRun) teststepRun).getAtomicRunResult());
        } else if (teststepRun instanceof DataDrivenTeststepRun) {
            DataDrivenTeststepRun dataDrivenTeststepRun = (DataDrivenTeststepRun) teststepRun;
            for (TeststepIndividualRun teststepIndividualRun: dataDrivenTeststepRun.getIndividualRuns()) {
                teststepIndividualRunDAO().insert(id, teststepIndividualRun);
            }
        }

        teststepRun.setId(id);
    }

    @SqlQuery("select * from teststep_run where testcase_run_id = :testcaseRunId")
    List<TeststepRun> _findByTestcaseRunId(@Bind("testcaseRunId") long testcaseRunId);

    default void resolveTeststepRuns(List<TeststepRun> stepRuns) {
        ListIterator<TeststepRun> stepRunsIterator = stepRuns.listIterator();
        while (stepRunsIterator.hasNext()) {
            TeststepRun stepRun = stepRunsIterator.next();
            List<TeststepIndividualRun> individualRuns = teststepIndividualRunDAO().findByTeststepRunId(stepRun.getId());
            if (individualRuns.size() > 0) {  //  it is a data driven test step run
                DataDrivenTeststepRun dataDrivenTeststepRun = new DataDrivenTeststepRun(stepRun);
                dataDrivenTeststepRun.setIndividualRuns(individualRuns);
                stepRunsIterator.set(dataDrivenTeststepRun);
            } else {                          //  it is a regular test step run
                RegularTeststepRun regularTeststepRun = new RegularTeststepRun(stepRun);
                regularTeststepRun.setAtomicRunResult(
                        teststepAtomicRunResultDAO().findFirstByTeststepRunId(stepRun.getId()));
                stepRunsIterator.set(regularTeststepRun);
            }
        }
    }

    default List<TeststepRun> findByTestcaseRunId(long testcaseRunId) {
        List<TeststepRun> stepRuns = _findByTestcaseRunId(testcaseRunId);
        resolveTeststepRuns(stepRuns);
        return stepRuns;
    }

    @SqlQuery("select * from teststep_run where testcase_individualrun_id = :testcaseIndividualRunId")
    List<TeststepRun> _findByTestcaseIndividualRunId(@Bind("testcaseIndividualRunId") long testcaseIndividualRunId);

    default List<TeststepRun> findByTestcaseIndividualRunId(long testcaseIndividualRunId) {
        List<TeststepRun> stepRuns = _findByTestcaseIndividualRunId(testcaseIndividualRunId);
        resolveTeststepRuns(stepRuns);
        return stepRuns;
    }

    @SqlQuery("select * from teststep_run where id = :id")
    TeststepRun findById(@Bind("id") long id);
}