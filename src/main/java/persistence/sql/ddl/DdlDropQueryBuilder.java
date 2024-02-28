package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.TableName;

public class DdlDropQueryBuilder {

    private final TableName tableName;
    private final Dialect dialect;

    public DdlDropQueryBuilder(final EntityMetaCreator entityMetaCreator, final Dialect dialect) {
        this.tableName = entityMetaCreator.createTableName();
        this.dialect = dialect;
    }

    public String dropDdl() {
        return String.format(dialect.getDropDefaultDdlQuery(), this.tableName.name());
    }
}
