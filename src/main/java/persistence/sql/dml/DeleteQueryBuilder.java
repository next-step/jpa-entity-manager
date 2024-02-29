package persistence.sql.dml;

import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.TableName;

public class DeleteQueryBuilder {

    public static final String DELETE_DEFAULT_DML = "delete from %s where %s";
    private final TableName tableName;
    private final PrimaryKey primaryKey;

    public DeleteQueryBuilder(TableName tableName, PrimaryKey primaryKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
    }

    public String createDeleteQuery(Object object) {
        return String.format(DELETE_DEFAULT_DML, this.tableName.name(), deleteWhere(object));
    }

    private String deleteWhere(Object object) {
        return String.format("%s = %s", this.primaryKey.name(), this.primaryKey.value(object));
    }

}
