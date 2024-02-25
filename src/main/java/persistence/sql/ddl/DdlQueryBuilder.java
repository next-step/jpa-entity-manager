package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;

public class DdlQueryBuilder {

    private final EntityTableMeta entityTableMeta;
    private final EntityColumnMeta entityColumnMeta;
    private final Dialect dialect;

    public DdlQueryBuilder(final Class<?> clazz, final Dialect dialect) {
        this.entityTableMeta = EntityTableMeta.of(clazz);
        this.entityColumnMeta = EntityColumnMeta.of(clazz, dialect);
        this.dialect = dialect;
    }

    public String createDdl() {
        return String.format(dialect.getCreateDefaultDdlQuery(), this.entityTableMeta.name(), this.entityColumnMeta.createColumnType());
    }

    public String dropDdl() {
        return String.format(dialect.getDropDefaultDdlQuery(), this.entityTableMeta.name());
    }
}
