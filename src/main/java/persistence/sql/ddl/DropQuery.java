package persistence.sql.ddl;

import persistence.sql.common.meta.TableName;

public class DropQuery {

    private static final String DEFAULT_DROP_QUERY = "DROP TABLE %s";

    private final TableName tableName;

    private DropQuery(TableName tableName) {
        this.tableName = tableName;
    }

    public static String drop(TableName tableName) {
        return new DropQuery(tableName).combineQuery();
    }

    private String combineQuery() {
        return String.format(DEFAULT_DROP_QUERY, tableName.getValue());
    }
}
