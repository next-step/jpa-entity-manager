package persistence.sql.ddl;

import persistence.sql.dialect.Dialect;
import persistence.sql.meta.simple.SimpleColumn;
import persistence.sql.meta.simple.SimplePrimaryKey;

import static persistence.sql.dialect.JavaClassCodeTypeMappings.getJavaClassToJdbcCodeType;

public class DdlGenerator {

    public static String createPrimaryKeyColumnsClause(final SimplePrimaryKey entityPrimaryKey, final Dialect dialect) {
        final String fieldName = entityPrimaryKey.name();

        final String dataType = dialect.getColumnType(getJavaClassToJdbcCodeType(entityPrimaryKey.type()));

        String keyCreateStrategy = dialect.generatorCreatePrimaryDdl(entityPrimaryKey.strategy());

        return String.format("%s %s %s", fieldName, dataType, keyCreateStrategy);
    }

    public static String columns(final SimpleColumn entityColumn, final Dialect dialect) {
        final String fieldName = entityColumn.getFieldName();

        final String dataType = dialect.getColumnType(getJavaClassToJdbcCodeType(entityColumn.type()));

        final String notNullConstraint =  dialect.getNotNullConstraint(entityColumn.isNullable());

        return String.format("%s %s %s", fieldName, dataType, notNullConstraint);
    }

    public static String constraint(final SimplePrimaryKey entityPrimaryKey, final Dialect dialect) {
        return String.format("primary key (%s)", entityPrimaryKey.name());
    }
}
