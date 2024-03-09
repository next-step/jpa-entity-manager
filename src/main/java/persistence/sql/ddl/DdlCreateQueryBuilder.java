package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Column;
import persistence.sql.meta.Columns;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.Table;

import java.util.stream.Collectors;

import static persistence.sql.dialect.JavaClassCodeTypeMappings.getJavaClassToJdbcCodeType;

public class DdlCreateQueryBuilder {

    private static final String CREATE_DEFAULT_DDL = "create table %s (%s)";
    public static final String COMMA = ", ";
    private final Dialect dialect;
    private final EntityMetaCreator entityMetaCreator;

    public DdlCreateQueryBuilder(final Dialect dialect, final EntityMetaCreator entityMetaCreator) {
        this.dialect = dialect;
        this.entityMetaCreator = entityMetaCreator;
    }

    public String createDdl(Class<?> clazz) {
        final Table table = entityMetaCreator.createByClass(clazz);
        return String.format(CREATE_DEFAULT_DDL, table.name(), createColumns(table));
    }

    private String createColumns(Table table) {
        return String.join(COMMA, primaryKeyClause(table.primaryKey()),
                columnClause(table.columns()),
                constraintClause(table.primaryKey()));
    }

    private String primaryKeyClause(PrimaryKey primaryKey) {
        return String.format("%s %s %s", primaryKey.name(),
                dialect.getColumnType(getJavaClassToJdbcCodeType(primaryKey.type())),
                dialect.generatorCreatePrimaryDdl(primaryKey.strategy()));
    }

    private String columnClause(Columns columns) {
        return columns.getColumns()
                .stream()
                .map(this::createColumnsClause)
                .collect(Collectors.joining(", "));
    }

    private String createColumnsClause(Column column) {
        return String.format("%s %s %s", column.getFieldName(),
                dialect.getColumnType(getJavaClassToJdbcCodeType(column.type())),
                dialect.getNotNullConstraint(column.isNullable()));
    }

    private String constraintClause(PrimaryKey primaryKey) {
        return String.format("primary key (%s)", primaryKey.name());
    }
}
