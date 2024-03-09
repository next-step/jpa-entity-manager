package persistence.sql.dml;

import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.Table;

public class DeleteQueryBuilder {

    public static final String DELETE_DEFAULT_DML = "delete from %s where %s";

    public DeleteQueryBuilder() {
    }

    public String createDeleteQuery(Table table) {

        return String.format(DELETE_DEFAULT_DML, table.name(), deleteWhere(table));
    }

    private String deleteWhere(Table table) {
        final PrimaryKey primaryKey = table.primaryKey();
        return String.format("%s = %s", primaryKey.name(), primaryKey.value());
    }
}
