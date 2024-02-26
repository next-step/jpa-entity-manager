package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityColumns;
import persistence.sql.meta.EntityPrimaryKey;
import persistence.sql.meta.EntityTableMeta;

import java.util.stream.Collectors;

public class DdlCreateQueryBuilder {

    public static final String COMMA = ", ";
    private final EntityTableMeta entityTableMeta;
    private final EntityPrimaryKey entityPrimaryKey;
    private final EntityColumns entityColumns;
    private final Dialect dialect;

    public DdlCreateQueryBuilder(final Class<?> clazz, final Dialect dialect) {
        this.entityTableMeta = EntityTableMeta.of(clazz);
        this.entityPrimaryKey = EntityPrimaryKey.of(clazz);
        this.entityColumns = EntityColumns.of(clazz);
        this.dialect = dialect;
    }

    public String createDdl() {
        return String.format(dialect.getCreateDefaultDdlQuery(), this.entityTableMeta.name(), createColumns());
    }

    private String createColumns() {
        return String.join(COMMA, primaryKeyColumns(), columns(), constraint());
    }

    private String primaryKeyColumns() {
        return DdlGenerator.createPrimaryKeyColumnsClause(this.entityPrimaryKey, dialect);
    }

    private String columns() {
        return this.entityColumns.getEntityColumns()
                .stream()
                .map(this::createColumnsClause)
                .collect(Collectors.joining(", "));
    }

    private String constraint() {
        return DdlGenerator.constraint(this.entityPrimaryKey, dialect);
    }

    private String createColumnsClause(EntityColumn e) {
        return DdlGenerator.columns(e, this.dialect);
    }

}
