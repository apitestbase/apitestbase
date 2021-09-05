package io.apitestbase.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apitestbase.models.teststep.apirequest.APIRequest;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public class ApiRequestArgumentFactory extends AbstractArgumentFactory<APIRequest> {
    public ApiRequestArgumentFactory() {
        super(Types.CLOB);
    }

    @Override
    protected Argument build(APIRequest value, ConfigRegistry config) {
        return (position, statement, ctx) -> {
            try {
                statement.setString(position, new ObjectMapper().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Fail to serialize the APIRequest object.");
            }
        };
    }
}
