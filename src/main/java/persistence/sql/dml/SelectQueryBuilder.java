package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Columns;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.TableName;

public class SelectQueryBuilder {

    private final TableName tableName;
    private final PrimaryKey primaryKey;
    private final Columns columns;
    private final Dialect dialect;

    public SelectQueryBuilder(EntityMetaCreator entityMetaCreator, final Dialect dialect) {
        this.tableName = entityMetaCreator.createTableName();
        this.primaryKey = entityMetaCreator.createPrimaryKey();
        this.columns = entityMetaCreator.createColumns();
        this.dialect = dialect;
    }

    public String createFindAllQuery() {
        return String.format(dialect.getFindAllDefaultDmlQuery(), select(), this.tableName.name());
    }

    public String createFindByIdQuery(Long id) {
        return String.format(dialect.getFindByIdDefaultDmlQuery(), createFindAllQuery(), selectWhere(id));
    }

    private String select() {
        return String.format("%s, %s", this.primaryKey.name(), String.join(", ", this.columns.names()));
    }

    private String selectWhere(Long id) {
        return String.format("%s = %dL", this.primaryKey.name(), id);
    }
}
