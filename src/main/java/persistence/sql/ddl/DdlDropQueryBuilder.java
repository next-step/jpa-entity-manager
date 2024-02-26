package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.EntityTableMeta;

public class DdlDropQueryBuilder {

    private final EntityTableMeta entityTableMeta;
    private final Dialect dialect;

    public DdlDropQueryBuilder(final Class<?> clazz, final Dialect dialect) {
        this.entityTableMeta = EntityTableMeta.of(clazz);
        this.dialect = dialect;
    }

    public String dropDdl() {
        return String.format(dialect.getDropDefaultDdlQuery(), this.entityTableMeta.name());
    }
}
