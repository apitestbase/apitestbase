package io.apitestbase.db;

import io.apitestbase.models.UDP;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UDPMapper implements RowMapper<UDP> {
    public UDP map(ResultSet rs, StatementContext ctx) throws SQLException {
        UDP udp = new UDP(
                rs.getLong("id"), rs.getShort("sequence"), rs.getString("name"), rs.getString("value"));

        return udp;
    }
}