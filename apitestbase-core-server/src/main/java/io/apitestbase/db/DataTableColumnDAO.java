package io.apitestbase.db;

import io.apitestbase.models.DataTableColumn;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

import static io.apitestbase.APITestBaseConstants.*;

@RegisterRowMapper(DataTableColumnMapper.class)
public interface DataTableColumnDAO extends CrossReferenceDAO {
    @SqlUpdate("CREATE SEQUENCE IF NOT EXISTS datatable_column_sequence START WITH 1 INCREMENT BY 1 NOCACHE")
    void createSequenceIfNotExists();

    @SqlUpdate("CREATE TABLE IF NOT EXISTS datatable_column (" +
            "id BIGINT DEFAULT datatable_column_sequence.NEXTVAL PRIMARY KEY, " +
            "name VARCHAR(200) NOT NULL DEFAULT 'COL' || DATEDIFF('MS', '1970-01-01', CURRENT_TIMESTAMP), " +
            "type VARCHAR(50) NOT NULL, sequence SMALLINT NOT NULL, testcase_id BIGINT NULL, teststep_id BIGINT NULL, " +
            "created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (testcase_id) REFERENCES testcase(id) ON DELETE CASCADE, " +
            "FOREIGN KEY (teststep_id) REFERENCES teststep(id) ON DELETE CASCADE, " +
            "CONSTRAINT DATATABLE_COLUMN_EXCLUSIVE_CONTAINER_TYPE_CONSTRAINT CHECK((testcase_id IS NULL AND teststep_id IS NOT NULL) OR (testcase_id IS NOT NULL AND teststep_id IS NULL)), " +
            "CONSTRAINT DATATABLE_COLUMN_CAPTION_COLUMN_UNRENAMEABLE_CONSTRAINT CHECK(NOT(sequence = 1 AND name <> 'Caption')), " +
            "CONSTRAINT DATATABLE_COLUMN_UNIQUE_SEQUENCE_CONSTRAINT UNIQUE(testcase_id, sequence), " +
            "CONSTRAINT DATATABLE_COLUMN_UNIQUE_SEQUENCE_CONSTRAINT2 UNIQUE(teststep_id, sequence), " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + " UNIQUE(testcase_id, name), " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_UNIQUE_NAME_CONSTRAINT_NAME_SUFFIX + "2 UNIQUE(teststep_id, name), " +
            "CONSTRAINT DATATABLE_COLUMN_" + DB_PROPERTY_NAME_CONSTRAINT_NAME_SUFFIX + " CHECK(" +
                CUSTOM_PROPERTY_NAME_CHECK + "))")
    void createTableIfNotExists();

    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) " +
            "select 'Caption', 'String', 1, id from testcase t " +
            "where (select count(*) from datatable_column where testcase_id = t.id) = 0")
    void insertCaptionColumnForTestcasesWithoutDataTableColumn();

    @SqlQuery("select * from datatable_column where testcase_id = :testcaseId order by sequence")
    List<DataTableColumn> findByTestcaseId(@Bind("testcaseId") long testcaseId);

    @SqlQuery("select * from datatable_column where teststep_id = :teststepId order by sequence")
    List<DataTableColumn> findByTeststepId(@Bind("teststepId") long teststepId);

    /**
     * @param testcaseId
     * @param teststepId
     * @param column
     * @param type for enum, name instead of value is bound by JDBI, so use a separate @Bind here instead of taking advantage of the @BindBean.
     * @return
     */
    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id, teststep_id) values (:c.name, :type, " +
            ":c.sequence, :testcaseId, :teststepId)")
    @GetGeneratedKeys("id")
    long insert(@Bind("testcaseId") Long testcaseId, @Bind("teststepId") Long teststepId,
                @BindBean("c") DataTableColumn column, @Bind("type") String type);

    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) values (:name, :type, " +
            "select coalesce(max(sequence), 0) + 1 from datatable_column where testcase_id = :testcaseId, :testcaseId)")
    @GetGeneratedKeys("id")
    long insertTestcaseDataTableColumnByImport(
            @Bind("testcaseId") long testcaseId, @Bind("name") String name, @Bind("type") String type);

    @SqlUpdate("insert into datatable_column (name, type, sequence, teststep_id) values (:name, :type, " +
            "select coalesce(max(sequence), 0) + 1 from datatable_column where teststep_id = :teststepId, :teststepId)")
    @GetGeneratedKeys("id")
    long insertTeststepDataTableColumnByImport(
            @Bind("teststepId") long teststepId, @Bind("name") String name, @Bind("type") String type);

    @SqlUpdate("insert into datatable_column (type, sequence, testcase_id, teststep_id) values (:type, " +
            "select max(sequence) + 1 from datatable_column " +
            "where (testcase_id is not null and testcase_id = :testcaseId) or " +
                "(teststep_id is not null and teststep_id = :teststepId), :testcaseId, :teststepId)")
    @GetGeneratedKeys("id")
    long _insert(@Bind("testcaseId") Long testcaseId, @Bind("teststepId") Long teststepId, @Bind("type") String type);

    @SqlUpdate("update datatable_column set name = :name where id = :id")
    void updateNameForInsert(@Bind("id") long id, @Bind("name") String name);

    @Transaction
    default void insert(Long testcaseId, Long teststepId, String columnType) {
        long id = _insert(testcaseId, teststepId, columnType);
        String name = "COL" + id;
        updateNameForInsert(id, name);

        dataTableCellDAO().insertCellsForNewColumn(testcaseId, teststepId, id);
    }

    @SqlUpdate("update datatable_column set name = :name, updated = CURRENT_TIMESTAMP where id = :id")
    void rename(@Bind("id") long id, @Bind("name") String name);

    @SqlUpdate("delete from datatable_column where id = :id")
    void delete(@Bind("id") long id);

    @SqlUpdate("insert into datatable_column (name, type, sequence, testcase_id) " +
            "select name, type, sequence, :targetTestcaseId from datatable_column where testcase_id = :sourceTestcaseId")
    void duplicateByTestcase(@Bind("sourceTestcaseId") long sourceTestcaseId,
                             @Bind("targetTestcaseId") long targetTestcaseId);

    @SqlQuery("select id from datatable_column where sequence = :sequence and (" +
            "(testcase_id is not null and testcase_id = :testcaseId) or " +
            "(teststep_id is not null and teststep_id = :teststepId))")
    long findIdBySequence(@Bind("testcaseId") Long testcaseId, @Bind("teststepId") Long teststepId,
                          @Bind("sequence") short sequence);

    @SqlUpdate("update datatable_column set sequence = :newSequence, updated = CURRENT_TIMESTAMP where id = :id")
    void updateSequenceById(@Bind("id") long id, @Bind("newSequence") short newSequence);

    @SqlUpdate("update datatable_column set " +
            "sequence = case when :direction = 'left' then sequence - 1 else sequence + 1 end, " +
            "updated = CURRENT_TIMESTAMP " +
            "where sequence >= :firstSequence and sequence <= :lastSequence and (" +
                "(testcase_id is not null and testcase_id = :testcaseId) or " +
                "(teststep_id is not null and teststep_id = :teststepId))")
    void batchMove(@Bind("testcaseId") Long testcaseId, @Bind("teststepId") Long teststepId,
                   @Bind("firstSequence") short firstSequence, @Bind("lastSequence") short lastSequence,
                   @Bind("direction") String direction);

    //  move column in testcase or teststep that contains the data table
    @Transaction
    default void moveInContainer(Long testcaseId, Long teststepId, short fromSequence, short toSequence) {
        if (fromSequence != toSequence) {
            long draggedColumnId = findIdBySequence(testcaseId, teststepId, fromSequence);

            //  shelve the dragged column first
            updateSequenceById(draggedColumnId, (short) -1);

            if (fromSequence < toSequence) {
                batchMove(testcaseId, teststepId, (short) (fromSequence + 1), toSequence, "left");
            } else {
                batchMove(testcaseId, teststepId, toSequence, (short) (fromSequence - 1), "right");
            }

            //  move the dragged column last
            updateSequenceById(draggedColumnId, toSequence);
        }
    }
}