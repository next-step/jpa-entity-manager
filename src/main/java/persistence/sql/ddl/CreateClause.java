package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Column;
import persistence.sql.meta.Columns;
import persistence.sql.meta.EntityMetaCreator;
import persistence.sql.meta.PrimaryKey;

import java.util.stream.Collectors;

import static persistence.sql.dialect.JavaClassCodeTypeMappings.getJavaClassToJdbcCodeType;

public class CreateClause {

    private final PrimaryKey primaryKey;
    private final Columns columns;
    private final Dialect dialect;

    public CreateClause(final EntityMetaCreator entityMetaCreator, final Dialect dialect) {
        this.primaryKey = entityMetaCreator.createPrimaryKey();
        this.columns = entityMetaCreator.createColumns();
        this.dialect = dialect;
    }

    public String primaryKeyClause() {
        final String fieldName = primaryKey.name();

        final String dataType = dialect.getColumnType(getJavaClassToJdbcCodeType(primaryKey.type()));

        String keyCreateStrategy = dialect.generatorCreatePrimaryDdl(primaryKey.strategy());

        return String.format("%s %s %s", fieldName, dataType, keyCreateStrategy);
    }

    public String columnClause() {
        return this.columns.getColumns()
                .stream()
                .map(this::createColumnsClause)
                .collect(Collectors.joining(", "));
    }

    public String constraintClause() {
        return String.format("primary key (%s)", primaryKey.name());
    }

    private String createColumnsClause(Column column) {
        final String fieldName = column.getFieldName();

        final String dataType = dialect.getColumnType(getJavaClassToJdbcCodeType(column.type()));

        final String notNullConstraint =  dialect.getNotNullConstraint(column.isNullable());

        return String.format("%s %s %s", fieldName, dataType, notNullConstraint);
    }
}
