package persistence.sql.dml;

import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

class InsertQuery {
    private static final String DEFAULT_INSERT_COLUMN_QUERY = "INSERT INTO %s (%s)";
    private static final String DEFAULT_INSERT_VALUE_QUERY = "VALUES(%s)";
    private TableName tableName;
    private Columns columns;
    private Values values;

    InsertQuery() { }

    String get(TableName tableName, Columns columns, Values values) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;

        return combineQuery();
    }

    /**
     * 해당 Class를 분석하여 INSERT QUERY로 조합합니다.
     */
    private String combineQuery() {
        return String.join(" ", parseColumns(), parseValues());
    }

    private String parseColumns() {
        return String.format(DEFAULT_INSERT_COLUMN_QUERY, tableName.getValue(), columns.getColumnsWithComma());
    }

    private String parseValues() {
        return String.format(DEFAULT_INSERT_VALUE_QUERY, values.getValuesWithComma());
    }
}
