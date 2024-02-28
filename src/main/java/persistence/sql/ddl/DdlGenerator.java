package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.Column;
import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.simple.SimpleColumn;
import persistence.sql.meta.simple.SimplePrimaryKey;

import static persistence.sql.dialect.JavaClassCodeTypeMappings.getJavaClassToJdbcCodeType;

public class DdlGenerator {

    public static String createPrimaryKeyColumnsClause(final PrimaryKey primaryKey, final Dialect dialect) {
        final String fieldName = primaryKey.name();

        final String dataType = dialect.getColumnType(getJavaClassToJdbcCodeType(primaryKey.type()));

        String keyCreateStrategy = dialect.generatorCreatePrimaryDdl(primaryKey.strategy());

        return String.format("%s %s %s", fieldName, dataType, keyCreateStrategy);
    }

    public static String columns(final Column column, final Dialect dialect) {
        final String fieldName = column.getFieldName();

        final String dataType = dialect.getColumnType(getJavaClassToJdbcCodeType(column.type()));

        final String notNullConstraint =  dialect.getNotNullConstraint(column.isNullable());

        return String.format("%s %s %s", fieldName, dataType, notNullConstraint);
    }

    public static String constraint(final PrimaryKey primaryKey) {
        return String.format("primary key (%s)", primaryKey.name());
    }
}
