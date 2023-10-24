package persistence.sql.ddl;

import persistence.sql.common.meta.TableName;

public class DropQuery {

    private static final String DEFAULT_DROP_QUERY = "DROP TABLE %s";

    private final TableName tableName;

    public <T> DropQuery(Class<T> tClass) {
        this.tableName = TableName.of(tClass);
    }

    public static <T> String drop(Class<T> tClass) {
        return new DropQuery(tClass).combineQuery();
    }

    private String combineQuery() {
        return String.format(DEFAULT_DROP_QUERY, tableName.getValue());
    }
}
