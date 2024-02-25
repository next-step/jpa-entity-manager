package persistence.sql.dml;

import persistence.sql.dialect.Dialect;

public class QueryBuilder {

    private final EntityTableMeta entityTableMeta;
    private final EntityColumns entityColumns;
    private final Dialect dialect;

    public QueryBuilder(Class<?> clazz, final Dialect dialect) {
        this.entityTableMeta = EntityTableMeta.of(clazz);
        this.entityColumns = EntityColumns.of(clazz);
        this.dialect = dialect;
    }

    public String createInsertQuery(Object object) {
        return String.format(dialect.getInsertDefaultDmlQuery(), this.entityTableMeta.name(), this.entityColumns.names(),
                entityColumns.insertValues(object, dialect));
    }

    public String createFindAllQuery() {
        return String.format(dialect.getFindAllDefaultDmlQuery(), this.entityColumns.names(), this.entityTableMeta.name());
    }

    public String createFindByIdQuery(Long id) {
        return String.format(dialect.getFindByIdDefaultDmlQuery(), createFindAllQuery(),
                this.entityColumns.primaryFieldName(), id);
    }

    public String createDeleteQuery(Object object) {
        return String.format(dialect.getDeleteDefaultDmlQuery(), this.entityTableMeta.name(),
                this.entityColumns.primaryFieldName(),
                this.entityColumns.primaryFieldValue(object));
    }
}
