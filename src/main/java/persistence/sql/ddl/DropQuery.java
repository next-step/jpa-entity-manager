package persistence.sql.ddl;

import persistence.sql.common.meta.TableName;

class DropQuery {

    private static final String DEFAULT_DROP_QUERY = "DROP TABLE %s";

    private TableName tableName;

    DropQuery() { }

    DropQuery create() {
        return new DropQuery();
    }

    String get(TableName tableName) {
        this.tableName = tableName;

        return combineQuery();
    }

    private String combineQuery() {
        return String.format(DEFAULT_DROP_QUERY, tableName.getValue());
    }
}
