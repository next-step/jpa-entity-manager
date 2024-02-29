package persistence.sql.dml;

import persistence.sql.meta.PrimaryKey;

public class DeleteQueryBuilder {

    public static final String DELETE_DEFAULT_DML = "delete from %s where %s";
    private final String tableName;
    private final PrimaryKey primaryKey;

    public DeleteQueryBuilder(String tableName, PrimaryKey primaryKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
    }

    public String createDeleteQuery(Object object) {
        return String.format(DELETE_DEFAULT_DML, this.tableName, deleteWhere(object));
    }

    private String deleteWhere(Object object) {
        return String.format("%s = %s", this.primaryKey.name(), this.primaryKey.value(object));
    }

}
