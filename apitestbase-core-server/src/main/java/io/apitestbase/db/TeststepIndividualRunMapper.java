package io.apitestbase.db;

import io.apitestbase.models.TestResult;
import io.apitestbase.models.testrun.TeststepIndividualRun;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeststepIndividualRunMapper implements RowMapper<TeststepIndividualRun> {
    public TeststepIndividualRun map(ResultSet rs, StatementContext ctx) throws SQLException {
        TeststepIndividualRun teststepIndividualRunRun = new TeststepIndividualRun();

        teststepIndividualRunRun.setId(rs.getLong("id"));
        teststepIndividualRunRun.setCaption(rs.getString("caption"));
        teststepIndividualRunRun.setStartTime(rs.getTimestamp("starttime"));
        teststepIndividualRunRun.setDuration(rs.getLong("duration"));
        teststepIndividualRunRun.setResult(TestResult.getByText(rs.getString("result")));

        return teststepIndividualRunRun;
    }
}
