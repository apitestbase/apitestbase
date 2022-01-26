package io.apitestbase.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apitestbase.core.teststep.APIResponse;
import io.apitestbase.models.assertion.AssertionVerification;
import io.apitestbase.models.testrun.teststeprun.TeststepAtomicRunResult;
import io.apitestbase.models.teststep.Teststep;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TeststepAtomicRunResultMapper implements RowMapper<TeststepAtomicRunResult> {
    public TeststepAtomicRunResult map(ResultSet rs, StatementContext ctx) throws SQLException {
        TeststepAtomicRunResult atomicRunResult = new TeststepAtomicRunResult();
        ObjectMapper objectMapper = new ObjectMapper();

        Teststep teststep;
        try {
            teststep = new ObjectMapper().readValue(rs.getString("teststep"), Teststep.class);
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize teststep JSON.", e);
        }
        atomicRunResult.setTeststep(teststep);

        APIResponse response;
        try {
            response = objectMapper.readValue(rs.getString("response"), APIResponse.class);
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize response JSON.", e);
        }
        atomicRunResult.setResponse(response);
        atomicRunResult.setInfoMessage(rs.getString("info_message"));
        atomicRunResult.setErrorMessage(rs.getString("error_message"));

        List<AssertionVerification> assertionVerifications;
        try {
            assertionVerifications = objectMapper.readValue(rs.getString("assertion_verifications"),
                    new TypeReference<List<AssertionVerification>>() { });
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize stepruns JSON.", e);
        }
        atomicRunResult.setAssertionVerifications(assertionVerifications);

        return atomicRunResult;
    }
}
