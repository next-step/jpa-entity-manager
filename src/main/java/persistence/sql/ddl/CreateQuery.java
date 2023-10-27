package persistence.sql.ddl;

import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

class CreateQuery {

    private static final String DEFAULT_CREATE_QUERY = "CREATE TABLE %s (%s)";
    private static final String DEFAULT_PRIMARY_KEY_QUERY = ", PRIMARY KEY (%s)";

    private TableName tableName;
    private Columns columns;

    CreateQuery() { }

    static CreateQuery create() {
        return new CreateQuery();
    }

    String get(TableName tableName, Columns columns) {
        this.tableName = tableName;
        this.columns = columns;

        return join();
    }

    private String join() {
        return String.join(", ", combineQuery());
    }

    /**
     * 해당 Class를 분석하여 CREATE QUERY로 조합합니다.
     */
    private String combineQuery() {
        return String.format(DEFAULT_CREATE_QUERY, tableName.getValue(),
            columns.getConstraintsWithColumns() + createColumns());
    }

    private String createColumns() {
        return String.format(DEFAULT_PRIMARY_KEY_QUERY, columns.getPrimaryKeyWithComma());
    }
}
