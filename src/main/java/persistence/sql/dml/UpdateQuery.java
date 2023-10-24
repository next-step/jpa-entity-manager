package persistence.sql.dml;

import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public class UpdateQuery {
    private static final String DEFAULT_UPDATE_COLUMN_QUERY = "UPDATE %s SET %s";

    private final TableName tableName;
    private final Columns columns;
    private final Values values;
    private final Object arg;

    public UpdateQuery(TableName tableName, Columns columns, Values values, Object arg) {
        this.tableName = tableName;
        this.columns = columns;
        this.values = values;
        this.arg = arg;
    }

    public static String create(Values values, TableName tableName, Columns columns, Object args) {
        return new UpdateQuery(tableName, columns, values, args).combine();
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
