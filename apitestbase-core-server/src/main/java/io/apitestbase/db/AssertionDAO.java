package io.apitestbase.db;

import io.apitestbase.models.Properties;
import io.apitestbase.models.assertion.Assertion;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;

import static io.apitestbase.APITestBaseConstants.DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX;

@RegisterRowMapper(AssertionMapper.class)
public interface AssertionDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS assertion_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS assertion (" +
            "id BIGINT DEFAULT assertion_sequence.NEXTVAL PRIMARY KEY, teststep_id BIGINT NOT NULL, " +
            "name VARCHAR(200) NOT NULL, type VARCHAR(50) NOT NULL, other_properties CLOB NOT NULL," +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE, " +
            "CONSTRAINT ASSERTION_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(teststep_id, name))")
    void createTableIfNotExists();

    @SqlUpdate("insert into assertion (teststep_id, name, type, other_properties) values " +
            "(:a.teststepId, :a.name, :a.type, :a.otherProperties)")
    @GetGeneratedKeys("id")
    long insert(@BindBean("a") Assertion assertion);

    @SqlUpdate("update assertion set name = :a.name, other_properties = :a.otherProperties, " +
            "updated = CURRENT_TIMESTAMP where id = :a.id")
    void update(@BindBean("a") Assertion assertion);

    @SqlQuery("select * from assertion where teststep_id = :teststepId")
    List<Assertion> findByTeststepId(@Bind("teststepId") long teststepId);

    @SqlQuery("select * from assertion where id = :id")
    Assertion findById(@Bind("id") long id);

    @SqlUpdate("delete from assertion where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlUpdate("delete from assertion where teststep_id = :teststepId")
    void deleteByTeststepId(@Bind("teststepId") long teststepId);

    @SqlUpdate("insert into assertion (teststep_id, name, type, other_properties) " +
            "select :newTeststepId, name, type, other_properties from assertion where teststep_id = :oldTeststepId")
    void duplicateByTeststep(@Bind("oldTeststepId") long oldTeststepId, @Bind("newTeststepId") long newTeststepId);

    @SqlUpdate("update assertion set other_properties = :otherProperties, updated = CURRENT_TIMESTAMP where id = :id")
    void updateOtherProperties(@Bind("id") long id, @Bind("otherProperties") Properties otherProperties);
}
