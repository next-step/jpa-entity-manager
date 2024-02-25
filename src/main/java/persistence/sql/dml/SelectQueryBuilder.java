package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.EntityColumns;
import persistence.sql.meta.EntityPrimaryKey;
import persistence.sql.meta.EntityTableMeta;

public class SelectQueryBuilder {
    private final EntityTableMeta entityTableMeta;
    private final EntityPrimaryKey entityPrimaryKey;
    private final EntityColumns entityColumns;
    private final Dialect dialect;

    public SelectQueryBuilder(final Class<?> clazz, final Dialect dialect) {
        this.entityTableMeta = EntityTableMeta.of(clazz);
        this.entityPrimaryKey = EntityPrimaryKey.of(clazz);
        this.entityColumns = EntityColumns.of(clazz);
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
