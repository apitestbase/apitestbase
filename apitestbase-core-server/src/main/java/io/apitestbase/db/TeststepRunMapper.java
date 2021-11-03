package io.apitestbase.db;

import io.apitestbase.models.TestResult;
import io.apitestbase.models.testrun.TeststepRun;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeststepRunMapper implements RowMapper<TeststepRun> {
    public TeststepRun map(ResultSet rs, StatementContext ctx) throws SQLException {
        TeststepRun teststepRun = new TeststepRun();

        teststepRun.setId(rs.getLong("id"));
        teststepRun.setStartTime(rs.getTimestamp("starttime"));
        teststepRun.setDuration(rs.getLong("duration"));
        teststepRun.setResult(TestResult.getByText(rs.getString("result")));

        return teststepRun;
    }
}
