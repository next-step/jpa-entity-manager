package persistence.sql.ddl;

import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryBuilder;

public class DropQueryBuilder extends QueryBuilder {
    public DropQueryBuilder(EntityMeta entityMeta, Dialect dialect) {
        super(entityMeta, dialect);
    }

    public String drop() {
        return dialect.dropTable(entityMeta.getTableName());
    }
}
