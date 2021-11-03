package io.apitestbase.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.testrun.TeststepAtomicRunResult;
import io.apitestbase.models.teststep.Teststep;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterRowMapper(TeststepAtomicRunResultMapper.class)
public interface TeststepAtomicRunResultDAO {
    @SqlUpdate("CREATE TABLE IF NOT EXISTS teststep_atomicrun_result (id IDENTITY PRIMARY KEY, " +
            "teststep_run_id BIGINT NOT NULL, teststep_individualrun_id BIGINT, teststep CLOB NOT NULL, " +
            "response CLOB, info_message CLOB, error_message CLOB, assertion_verifications CLOB, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_run_id) REFERENCES teststep_run(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (teststep_individualrun_id) REFERENCES teststep_individualrun(id) ON DELETE CASCADE)")
    void createTableIfNotExists();

    @SqlUpdate("insert into teststep_atomicrun_result (teststep_run_id, teststep_individualrun_id, teststep, " +
            "response, info_message, error_message, assertion_verifications) values (:teststepRunId, " +
            ":teststepIndividualRunId, :t.teststep, :response, :t.infoMessage, :t.errorMessage, :assertionVerifications)")
    @GetGeneratedKeys("id")
    long _insert(@Bind("teststepRunId") long teststepRunId,
                 @Bind("teststepIndividualRunId") Long teststepIndividualRunId,
                 @BindBean("t") TeststepAtomicRunResult atomicRunResult,
                 @Bind("response") String response, @Bind("assertionVerifications") String assertionVerifications);

    default void insert(long teststepRunId, Long teststepIndividualRunId, TeststepAtomicRunResult atomicRunResult)
            throws JsonProcessingException {
        //  remove contents that are not to be serialized into the teststep column
        Teststep teststep = atomicRunResult.getTeststep();
        teststep.getAssertions().clear();
        Endpoint endpoint = teststep.getEndpoint();
        if (endpoint != null) {
            endpoint.setPassword(null);
        }
        teststep.setDataTable(null);

        ObjectMapper objectMapper = new ObjectMapper();
        String responseStr = objectMapper.writeValueAsString(atomicRunResult.getResponse());
        String assertionVeStr = objectMapper.writeValueAsString(atomicRunResult.getAssertionVerifications());
        _insert(teststepRunId, teststepIndividualRunId, atomicRunResult, responseStr, assertionVeStr);
    }

    @SqlQuery("select * from teststep_atomicrun_result where teststep_run_id = :teststepRunId")
    TeststepAtomicRunResult findFirstByTeststepRunId(@Bind("teststepRunId") long teststepRunId);

    @SqlQuery("select * from teststep_atomicrun_result where teststep_individualrun_id = :teststepIndividualRunId")
    TeststepAtomicRunResult findByTeststepIndividualRunId(@Bind("teststepIndividualRunId") long teststepIndividualRunId);
}