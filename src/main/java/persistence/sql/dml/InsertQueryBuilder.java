package persistence.sql.dml;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.simple.SimpleColumns;
import persistence.sql.meta.simple.SimpleTableName;

public class InsertQueryBuilder {
    public static final String COMMA = ", ";
    private final SimpleTableName entityTableMeta;
    private final SimpleColumns entityColumns;
    private final Dialect dialect;

    public InsertQueryBuilder(Class<?> clazz, Dialect dialect) {
        this.entityTableMeta = SimpleTableName.of(clazz);
        this.entityColumns = SimpleColumns.of(clazz);
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
