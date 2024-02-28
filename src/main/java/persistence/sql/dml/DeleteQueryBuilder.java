package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.TableName;

public class DeleteQueryBuilder {

    private final TableName tableName;
    private final PrimaryKey primaryKey;
    private final Dialect dialect;

    public DeleteQueryBuilder(EntityMetaCreator entityMetaCreator, Dialect dialect) {
        this.tableName = entityMetaCreator.createTableName();
        this.primaryKey = entityMetaCreator.createPrimaryKey();
        this.dialect = dialect;
    }

    public String createDeleteQuery(Object object) {
        return String.format(dialect.getDeleteDefaultDmlQuery(), this.tableName.name(), deleteWhere(object));
    }

    private String deleteWhere(Object object) {
        return String.format("%s = %s", this.primaryKey.name(), this.primaryKey.value(object));
    }

}
