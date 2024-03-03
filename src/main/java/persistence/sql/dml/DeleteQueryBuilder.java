package persistence.sql.dml;

import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.simple.Table;

public class DeleteQueryBuilder {

    public static final String DELETE_DEFAULT_DML = "delete from %s where %s";

    public DeleteQueryBuilder() {
    }

    public String createDeleteQuery(Object object) {
        final Table table = Table.ofInstance(object);

        return String.format(DELETE_DEFAULT_DML, table.name(), deleteWhere(table, object));
    }

    private String deleteWhere(Table table, Object object) {
        final PrimaryKey primaryKey = table.primaryKey();
        return String.format("%s = %s", primaryKey.name(), primaryKey.value(object));
    }

}
