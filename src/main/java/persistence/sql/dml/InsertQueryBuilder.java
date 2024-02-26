package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.EntityColumns;
import persistence.sql.meta.EntityTableMeta;

public class InsertQueryBuilder {
    public static final String COMMA = ", ";
    private final EntityTableMeta entityTableMeta;
    private final EntityColumns entityColumns;
    private final Dialect dialect;

    public InsertQueryBuilder(Class<?> clazz, Dialect dialect) {
        this.entityTableMeta = EntityTableMeta.of(clazz);
        this.entityColumns = EntityColumns.of(clazz);
        this.dialect = dialect;
    }

    // insert into %s (%s) values (%s)
    public String createInsertQuery(Object object) {
        return String.format(dialect.getInsertDefaultDmlQuery(), tableName(), insertColumns(), insertValues(object));
    }
    private String tableName() {
        return entityTableMeta.name();
    }
    private String insertColumns() {
        return String.join(COMMA, this.entityColumns.names());
    }
    private String insertValues(Object object) {
        return String.join(COMMA, this.entityColumns.values(object));
    }
}
