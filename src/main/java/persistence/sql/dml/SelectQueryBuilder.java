package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.simple.SimpleColumns;
import persistence.sql.meta.simple.SimplePrimaryKey;
import persistence.sql.meta.simple.SimpleTableName;

public class SelectQueryBuilder {
    private final SimpleTableName entityTableMeta;
    private final SimplePrimaryKey entityPrimaryKey;
    private final SimpleColumns entityColumns;
    private final Dialect dialect;

    public SelectQueryBuilder(final Class<?> clazz, final Dialect dialect) {
        this.entityTableMeta = SimpleTableName.of(clazz);
        this.entityPrimaryKey = SimplePrimaryKey.of(clazz);
        this.entityColumns = SimpleColumns.of(clazz);
        this.dialect = dialect;
    }

    //select %s from %s
    public String createFindAllQuery() {
        return String.format(dialect.getFindAllDefaultDmlQuery(), select(), this.entityTableMeta.name());
    }
    // where %s = %dL
    public String createFindByIdQuery(Long id) {
        return String.format(dialect.getFindByIdDefaultDmlQuery(), createFindAllQuery(), selectWhere(id));
    }
    private String select() {
        return String.format("%s, %s", this.entityPrimaryKey.name(), String.join(", ", this.entityColumns.names()));
    }
    private String selectWhere(Long id) {
        return String.format("%s = %dL", this.entityPrimaryKey.name(), id);
    }
}
