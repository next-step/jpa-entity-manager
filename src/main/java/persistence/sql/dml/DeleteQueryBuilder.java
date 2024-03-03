package persistence.sql.dml;

import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;
import persistence.sql.meta.simple.SimpleTable;

public class DeleteQueryBuilder {

    public static final String DELETE_DEFAULT_DML = "delete from %s where %s";

    public DeleteQueryBuilder() {
    }

    public String createDeleteQuery(Object object) {
        final SimpleTable table = SimpleEntityMetaCreator.tableOfInstance(object);

        return String.format(DELETE_DEFAULT_DML, table.name(), deleteWhere(table));
    }

    private String deleteWhere(SimpleTable table) {
        final PrimaryKey primaryKey = table.primaryKey();
        return String.format("%s = %s", primaryKey.name(), primaryKey.value());
    }
}
