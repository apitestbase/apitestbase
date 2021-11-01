package io.apitestbase.db;

import io.apitestbase.models.TestResult;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public class TestResultArgumentFactory extends AbstractArgumentFactory<TestResult> {
    public TestResultArgumentFactory() {
        super(Types.VARCHAR);
    }

    @Override
    protected Argument build(TestResult value, ConfigRegistry config) {
        return (position, statement, ctx) -> statement.setString(position, value.toString());
    }
}
