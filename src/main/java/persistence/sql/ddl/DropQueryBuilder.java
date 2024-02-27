package persistence.sql.ddl;

import persistence.sql.meta.Table;

public class DropQueryBuilder {

    private static final String DROP_TABLE_DEFINITION = "DROP TABLE %s";

    private DropQueryBuilder() {
    }

    public static DropQueryBuilder from() {
        return new DropQueryBuilder();
    }

    public String generateQuery(Table table) {
        return String.format(DROP_TABLE_DEFINITION, table.getTableName());
    }
}
