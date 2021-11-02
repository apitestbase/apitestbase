package io.apitestbase.db;

import io.apitestbase.models.DataTable;
import io.apitestbase.models.DataTableCell;
import io.apitestbase.models.DataTableColumn;
import io.apitestbase.models.DataTableColumnType;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.*;

public interface DataTableDAO extends CrossReferenceDAO {
    /**
     * Caption column is the initial column in a data table.
     * @param testcaseId
     */
    default void createCaptionColumn(Long testcaseId, Long teststepId) {
        DataTableColumn dataTableColumn = new DataTableColumn();
        dataTableColumn.setName(DataTableColumn.COLUMN_NAME_CAPTION);
        dataTableColumn.setSequence((short) 1);
        dataTableColumnDAO().insert(testcaseId, teststepId, dataTableColumn, DataTableColumnType.STRING.toString());
    }

    //  populate the data table rows Java model column by column
    default void populateColumnsAndRows(DataTable dataTable, List<DataTableColumn> columns, boolean fetchFirstRowOnly) {
        List<LinkedHashMap<String, DataTableCell>> rows = new ArrayList<>();
        Map<Short, LinkedHashMap<String, DataTableCell>> rowSequenceMap = new HashMap<>();  //  map rowSequence to row object (because rowSequence is not consecutive)
        for (DataTableColumn column: columns) {
            List<DataTableCell> cellsInColumn = dataTableCellDAO().findByColumnId(column.getId());
            for (DataTableCell cellInColumn: cellsInColumn) {
                short rowSequence = cellInColumn.getRowSequence();

                if (column.getType() != DataTableColumnType.STRING && cellInColumn.getEndpoint() != null) {
                    cellInColumn.setEndpoint(endpointDAO().findById_NotMaskingPassword(cellInColumn.getEndpoint().getId()));
                }

                if (!rowSequenceMap.containsKey(rowSequence)) {
                    LinkedHashMap<String, DataTableCell> row = new LinkedHashMap<>();
                    rowSequenceMap.put(rowSequence, row);
                    rows.add(row);
                }
                rowSequenceMap.get(rowSequence).put(column.getName(), cellInColumn);

                if (fetchFirstRowOnly && rows.size() == 1) {
                    break;
                }
            }
        }

        dataTable.setColumns(columns);
        dataTable.setRows(rows);
    }

    /**
     * @param testcaseId
     * @param fetchFirstRowOnly if true, only the first data table row (if exists) will be fetched; if false, all rows will be fetched.
     * @return
     */
    @Transaction
    default DataTable getTestcaseDataTable(long testcaseId, boolean fetchFirstRowOnly) {
        List<DataTableColumn> columns = dataTableColumnDAO().findByTestcaseId(testcaseId);

        DataTable dataTable = new DataTable();
        populateColumnsAndRows(dataTable, columns, fetchFirstRowOnly);

        return dataTable;
    }

    /**
     * @param teststepId
     * @param fetchFirstRowOnly if true, only the first data table row (if exists) will be fetched; if false, all rows will be fetched.
     * @return
     */
    @Transaction
    default DataTable getTeststepDataTable(long teststepId, boolean fetchFirstRowOnly) {
        List<DataTableColumn> columns = dataTableColumnDAO().findByTeststepId(teststepId);

        DataTable dataTable = new DataTable();
        populateColumnsAndRows(dataTable, columns, fetchFirstRowOnly);

        return dataTable;
    }

    default void duplicateColumns(List<DataTableColumn> sourceColumns, List<DataTableColumn> targetColumns) {
        for (DataTableColumn targetColumn: targetColumns) {
            long sourceColumnId = -1;
            for (DataTableColumn sourceColumn: sourceColumns) {
                if (sourceColumn.getName().equals(targetColumn.getName())) {
                    sourceColumnId = sourceColumn.getId();
                    break;
                }
            }
            dataTableCellDAO().duplicateByColumn(sourceColumnId, targetColumn.getId());
        }
    }

    @Transaction
    default void duplicateByTestcase(long sourceTestcaseId, long targetTestcaseId) {
        dataTableColumnDAO().duplicateByTestcase(sourceTestcaseId, targetTestcaseId);
        List<DataTableColumn> sourceColumns = dataTableColumnDAO().findByTestcaseId(sourceTestcaseId);
        List<DataTableColumn> targetColumns = dataTableColumnDAO().findByTestcaseId(targetTestcaseId);
        duplicateColumns(sourceColumns, targetColumns);
    }

    @Transaction
    default void duplicateByTeststep(long sourceTeststepId, long targetTeststepId) {
        dataTableColumnDAO().duplicateByTeststep(sourceTeststepId, targetTeststepId);
        List<DataTableColumn> sourceColumns = dataTableColumnDAO().findByTeststepId(sourceTeststepId);
        List<DataTableColumn> targetColumns = dataTableColumnDAO().findByTeststepId(targetTeststepId);
        duplicateColumns(sourceColumns, targetColumns);
    }

    @Transaction
    default void insertTestcaseDataTableByImport(long testcaseId, DataTable dataTable) {
        for (DataTableColumn column: dataTable.getColumns()) {
            String columnName = column.getName();
            long columnId = dataTableColumnDAO().insertTestcaseDataTableColumnByImport(
                    testcaseId, columnName, column.getType().toString());
            insertDataTableRows(columnId, columnName, dataTable.getRows());
        }
    }

    @Transaction
    default void insertTeststepDataTableByImport(long teststepId, DataTable dataTable) {
        if (dataTable == null) {
            dataTableDAO().createCaptionColumn(null, teststepId);
        } else {
            for (DataTableColumn column : dataTable.getColumns()) {
                String columnName = column.getName();
                long columnId = dataTableColumnDAO().insertTeststepDataTableColumnByImport(
                        teststepId, columnName, column.getType().toString());
                insertDataTableRows(columnId, columnName, dataTable.getRows());
            }
        }
    }

    default void insertDataTableRows(long columnId, String columnName, List<LinkedHashMap<String, DataTableCell>> rows) {
        for (LinkedHashMap<String, DataTableCell> row: rows) {
            for (Map.Entry<String, DataTableCell> cellEntry: row.entrySet()) {
                if (cellEntry.getKey().equals(columnName)) {
                    dataTableCellDAO().insert(columnId, cellEntry.getValue());
                    break;
                }
            }
        }
    }
}
