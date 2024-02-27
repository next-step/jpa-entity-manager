package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.simple.SimpleTableName;

public class DdlDropQueryBuilder {

    private final SimpleTableName entityTableMeta;
    private final Dialect dialect;

    public DdlDropQueryBuilder(final Class<?> clazz, final Dialect dialect) {
        this.entityTableMeta = SimpleTableName.of(clazz);
        this.dialect = dialect;
    }

    public String dropDdl() {
        return String.format(dialect.getDropDefaultDdlQuery(), this.entityTableMeta.name());
    }
}
