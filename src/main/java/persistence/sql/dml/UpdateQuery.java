package persistence.sql.dml;

import persistence.sql.common.instance.InstanceManager;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public class UpdateQuery {
    private static final String DEFAULT_UPDATE_COLUMN_QUERY = "UPDATE %s SET %s";

    private final TableName tableName;
    private final Columns columns;
    private Object arg;

    public UpdateQuery(TableName tableName, Columns columns, Object arg) {
        this.tableName = tableName;
        this.columns = columns;
        this.arg = arg;
    }

    public static <T> String create(T t, TableName tableName, Columns columns, Object args) {
        return new UpdateQuery(tableName, columns, args).combine(t);
    }

    private <T> String combine(T t) {
        return String.join(" ", getTableQuery(t), getCondition());
    }

    private <T> String getTableQuery(T t) {
        return String.format(DEFAULT_UPDATE_COLUMN_QUERY, tableName.getValue(), setChangeField(t));
    }

    private <T> String setChangeField(T t) {
        return InstanceManager.getFieldNameAndValue(t);
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
