package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.simple.SimpleColumn;
import persistence.sql.meta.simple.SimpleColumns;
import persistence.sql.meta.simple.SimplePrimaryKey;
import persistence.sql.meta.simple.SimpleTableName;

import java.util.stream.Collectors;

public class DdlCreateQueryBuilder {

    public static final String COMMA = ", ";
    private final SimpleTableName entityTableMeta;
    private final SimplePrimaryKey entityPrimaryKey;
    private final SimpleColumns entityColumns;
    private final Dialect dialect;

    public DdlCreateQueryBuilder(final Class<?> clazz, final Dialect dialect) {
        this.entityTableMeta = SimpleTableName.of(clazz);
        this.entityPrimaryKey = SimplePrimaryKey.of(clazz);
        this.entityColumns = SimpleColumns.of(clazz);
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
        return this.entityColumns.getColumns()
                .stream()
                .map(this::createColumnsClause)
                .collect(Collectors.joining(", "));
    }

    private String constraint() {
        return DdlGenerator.constraint(this.entityPrimaryKey, dialect);
    }

    private String createColumnsClause(SimpleColumn e) {
        return DdlGenerator.columns(e, this.dialect);
    }

}
