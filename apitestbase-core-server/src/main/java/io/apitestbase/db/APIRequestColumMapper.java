package io.apitestbase.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class APIRequestColumMapper implements ColumnMapper<APIRequest> {
    @Override
    public APIRequest map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        try {
            return new ObjectMapper().readValue(r.getString(columnNumber), APIRequest.class);
        } catch (IOException e) {
            throw new SQLException("Failed to deserialize api_request JSON.", e);
        }
    }
}
