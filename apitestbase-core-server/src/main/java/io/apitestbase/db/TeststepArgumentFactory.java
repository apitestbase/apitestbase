package io.apitestbase.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apitestbase.models.teststep.Teststep;
import io.apitestbase.utils.GeneralUtils;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public class TeststepArgumentFactory extends AbstractArgumentFactory<Teststep> {
    public TeststepArgumentFactory() {
        super(Types.CLOB);
    }

    @Override
    protected Argument build(Teststep value, ConfigRegistry config) {
        return (position, statement, ctx) -> {
            ObjectMapper objectMapper = new ObjectMapper();
            GeneralUtils.addMixInsForWireMock(objectMapper);
            try {
                statement.setString(position, objectMapper.writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize the Teststep object.");
            }
        };
    }
}
