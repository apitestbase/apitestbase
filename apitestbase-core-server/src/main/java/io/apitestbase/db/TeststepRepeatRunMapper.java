package io.apitestbase.db;

import io.apitestbase.models.TestResult;
import io.apitestbase.models.testrun.teststeprun.TeststepRepeatRun;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TeststepRepeatRunMapper implements RowMapper<TeststepRepeatRun> {
    @Override
    public TeststepRepeatRun map(ResultSet rs, StatementContext ctx) throws SQLException {
        TeststepRepeatRun teststepRepeatRun = new TeststepRepeatRun();

        teststepRepeatRun.setId(rs.getLong("id"));
        teststepRepeatRun.setIndex(rs.getInt("index"));
        teststepRepeatRun.setStartTime(rs.getTimestamp("starttime"));
        teststepRepeatRun.setDuration(rs.getLong("duration"));
        teststepRepeatRun.setResult(TestResult.getByText(rs.getString("result")));

        return teststepRepeatRun;
    }
}
