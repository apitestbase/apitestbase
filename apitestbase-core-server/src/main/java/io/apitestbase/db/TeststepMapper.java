package io.apitestbase.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import io.apitestbase.utils.GeneralUtils;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class TeststepMapper implements RowMapper<Teststep> {
    public Teststep map(ResultSet rs, StatementContext ctx) throws SQLException {
        List<String> fields = GeneralUtils.getFieldsPresentInResultSet(rs);

        Teststep teststep;
        String type = rs.getString("type");
        if (!Teststep.TYPE_HTTP.equals(type) && !Teststep.TYPE_SOAP.equals(type) && !Teststep.TYPE_DB.equals(type) &&
                fields.contains("other_properties") && rs.getString("other_properties") != null) {
            String tempTeststepJSON = "{\"type\":\"" + type + "\",\"otherProperties\":" +
                    rs.getString("other_properties") + "}";
            try {
                teststep = new ObjectMapper().readValue(tempTeststepJSON, Teststep.class);
            } catch (IOException e) {
                throw new SQLException("Failed to deserialize other_properties JSON.", e);
            }
        } else {
            teststep = new Teststep();
        }

        teststep.setId(rs.getLong("id"));
        teststep.setTestcaseId(rs.getLong("testcase_id"));
        teststep.setSequence(rs.getShort("sequence"));
        teststep.setName(rs.getString("name"));
        teststep.setType(type);
        teststep.setDescription(rs.getString("description"));
        teststep.setAction(fields.contains("action") ? rs.getString("action") : null);

        if (!Teststep.TYPE_HTTP.equals(type) && !Teststep.TYPE_SOAP.equals(type) && !Teststep.TYPE_MQ.equals(type) &&
                !Teststep.TYPE_DB.equals(type) && fields.contains("request")) {
            byte[] requestBytes = rs.getBytes("request");
            if (requestBytes != null) {
                Object request = new String(requestBytes);
                teststep.setRequest(request);
            }
        }

        if (fields.contains("api_request") && rs.getString("api_request") != null) {
            try {
                teststep.setApiRequest(new ObjectMapper().readValue(rs.getString("api_request"), APIRequest.class));
            } catch (IOException e) {
                throw new SQLException("Failed to deserialize api_request JSON.", e);
            }
        }

        if (fields.contains("endpoint_id")) {
            Endpoint endpoint = new Endpoint();
            endpoint.setId(rs.getLong("endpoint_id"));
            teststep.setEndpoint(endpoint);
        }
        teststep.setEndpointProperty(fields.contains("endpoint_property") ? rs.getString("endpoint_property") : null);

        return teststep;
    }
}
