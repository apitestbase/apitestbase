package io.apitestbase.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.*;

@JsonView({ResourceJsonViews.DataTableUIGrid.class, ResourceJsonViews.TestcaseExport.class})
public class DataTable {
    private List<DataTableColumn> columns = new ArrayList<>();
    private List<LinkedHashMap<String, DataTableCell>> rows = new ArrayList<>();

    public List<DataTableColumn> getColumns() {
        return columns;
    }

    public List<String> getNonCaptionColumnNames() {
        List<String> columnNames = new ArrayList<>();
        for (DataTableColumn column : columns) {
            if (!DataTableColumn.COLUMN_NAME_CAPTION.equals(column.getName())) {
                columnNames.add(column.getName());
            }
        }
        return columnNames;
    }

    public void setColumns(List<DataTableColumn> columns) {
        this.columns = columns;
    }

    public List<LinkedHashMap<String, DataTableCell>> getRows() {
        return rows;
    }

    public void setRows(List<LinkedHashMap<String, DataTableCell>> rows) {
        this.rows = rows;
    }

    @JsonIgnore
    public DataTableColumnType getColumnTypeByName(String columnName) {
        DataTableColumnType columnType = null;
        for (DataTableColumn column: columns) {
            if (column.getName().equals(columnName)) {
                columnType = column.getType();
                break;
            }
        }
        return columnType;
    }

    public Map<String, String> getStringPropertiesInRow(int rowIndex) {
        Map<String, String> result = new HashMap<>();
        LinkedHashMap<String, DataTableCell> row = rows.get(rowIndex);
        for (Map.Entry<String, DataTableCell> property: row.entrySet()) {
            if (!DataTableColumn.COLUMN_NAME_CAPTION.equals(property.getKey()) &&
                    getColumnTypeByName(property.getKey()) == DataTableColumnType.STRING) {
                result.put(property.getKey(), property.getValue().getValue());
            }
        }
        return result;
    }

    public Map<String, Endpoint> getEndpointPropertiesInRow(int rowIndex) {
        Map<String, Endpoint> result = new HashMap<>();
        LinkedHashMap<String, DataTableCell> row = rows.get(rowIndex);
        for (Map.Entry<String, DataTableCell> property: row.entrySet()) {
            if (getColumnTypeByName(property.getKey()) != DataTableColumnType.STRING) {
                result.put(property.getKey(), property.getValue().getEndpoint());
            }
        }
        return result;
    }
}
