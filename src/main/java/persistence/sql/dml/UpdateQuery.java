package persistence.sql.dml;

import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

class UpdateQuery {
    private static final String DEFAULT_UPDATE_COLUMN_QUERY = "UPDATE %s SET %s";

    private TableName tableName;
    private Columns columns;
    private Values values;
    private Object arg;

    UpdateQuery() { }

    String get(Values values, TableName tableName, Columns columns, Object args) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
        this.arg = args;

        return combine();
    }

    private String combine() {
        return String.join(" ", getTableQuery(), getCondition());
    }

    private String getTableQuery() {
        return String.format(DEFAULT_UPDATE_COLUMN_QUERY, tableName.getValue(), setChangeField());
    }

    private String setChangeField() {
        return values.getFieldNameAndValue(columns);
    }

    private String getCondition() {
        String condition = ConditionBuilder.getCondition(columns.getIdName(), arg);
        return condition.replace(" id ", " " + setConditionField("id") + " ");
    }

    private String setConditionField(String word) {
        if (word.equals("id")) {
            word = columns.getIdName();
        }
        return word;
    }
}
