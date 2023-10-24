package persistence.sql.dml;

import persistence.sql.common.instance.InstanceManager;
import persistence.sql.common.instance.Value;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.EntityMeta;
import persistence.sql.common.meta.TableName;

public class InsertQuery {
    private static final String DEFAULT_INSERT_COLUMN_QUERY = "INSERT INTO %s (%s)";
    private static final String DEFAULT_INSERT_VALUE_QUERY = "VALUES(%s)";
    private EntityMeta entityMeta;
    private TableName tableName;
    private Columns columns;

    protected <T> InsertQuery(T t) {
        this.entityMeta = EntityMeta.of(t.getClass());
    }

    protected InsertQuery(TableName tableName, Columns columns) {
        this.tableName = tableName;
        this.columns = columns;
    }

    public static <T> String create(T t) {
        return new InsertQuery(t).combineQuery(t);
    }

    public static <T> String create(TableName tableName, Columns columns, T t) {
        return new InsertQuery(tableName, columns).combineQuery(t);
    }

    /**
     * 해당 Class를 분석하여 INSERT QUERY로 조합합니다.
     */
    private <T> String combineQuery(T t) {
        return String.join(" ", parseColumns(), parseValues(t));
    }

    private String parseColumns() {
        return String.format(DEFAULT_INSERT_COLUMN_QUERY, tableName.getValue(), columns.getColumnsWithComma());
    }

    private <T> String parseValues(T t) {
        return String.format(DEFAULT_INSERT_VALUE_QUERY, InstanceManager.getValuesWithComma(Value.of(t)));
    }
}
