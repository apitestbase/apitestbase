package io.apitestbase.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.apitestbase.models.teststep.TeststepRunPattern;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public class TeststepRunPatternArgumentFactory extends AbstractArgumentFactory<TeststepRunPattern> {
    public TeststepRunPatternArgumentFactory() {
        super(Types.CLOB);
    }

    @Override
    protected Argument build(TeststepRunPattern value, ConfigRegistry config) {
        return (position, statement, ctx) -> {
            try {
                statement.setString(position, new ObjectMapper().writeValueAsString(value));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize the TeststepRunPattern object.");
            }
        };
    }
}
