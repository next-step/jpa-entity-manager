package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Column;
import persistence.sql.meta.Columns;
import persistence.sql.meta.PrimaryKey;

import java.util.stream.Collectors;

import static persistence.sql.dialect.JavaClassCodeTypeMappings.getJavaClassToJdbcCodeType;

public class DdlCreateQueryBuilder {

    private static final String CREATE_DEFAULT_DDL = "create table %s (%s)";
    public static final String COMMA = ", ";
    private final String tableName;
    private final PrimaryKey primaryKey;
    private final Columns columns;
    private final Dialect dialect;

    public DdlCreateQueryBuilder(String tableName, PrimaryKey primaryKey, Columns columns, Dialect dialect) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.columns = columns;
        this.dialect = dialect;
    }

    public String createDdl() {
        return String.format(CREATE_DEFAULT_DDL, this.tableName, createColumns());
    }

    private String createColumns() {
        return String.join(COMMA, primaryKeyClause(), columnClause(), constraintClause());
    }

    private String primaryKeyClause() {
        return String.format("%s %s %s", this.primaryKey.name(),
                dialect.getColumnType(getJavaClassToJdbcCodeType(primaryKey.type())),
                dialect.generatorCreatePrimaryDdl(primaryKey.strategy()));
    }

    private String columnClause() {
        return this.columns.getColumns()
                .stream()
                .map(this::createColumnsClause)
                .collect(Collectors.joining(", "));
    }

    private String createColumnsClause(Column column) {
        return String.format("%s %s %s", column.getFieldName(),
                dialect.getColumnType(getJavaClassToJdbcCodeType(column.type())),
                dialect.getNotNullConstraint(column.isNullable()));
    }

    private String constraintClause() {
        return String.format("primary key (%s)", primaryKey.name());
    }
}
