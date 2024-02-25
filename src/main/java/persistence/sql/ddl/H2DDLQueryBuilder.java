package persistence.sql.ddl;

import jakarta.persistence.Column;
import persistence.inspector.ClsssMetadataInspector;
import persistence.inspector.EntityFieldInspector;
import persistence.inspector.EntityInfoExtractor;
import persistence.sql.Dialect;
import persistence.sql.h2.H2Dialect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class H2DDLQueryBuilder implements DDLQueryBuilder {
    private final Dialect dialect;

    public H2DDLQueryBuilder() {
        this.dialect = new H2Dialect();
    }

    @Override
    public String createTableQuery(Class<?> clazz) {

        return createTableQuery(getTableName(clazz), createColumnClause(clazz), createPrimaryKeyClause(clazz));
    }

    private String createTableQuery(String tableName, String columnClause, String primaryKeyClause) {

        return String.format("CREATE TABLE %s (%s%s)", tableName, columnClause, primaryKeyClause);
    }

    @Override
    public String dropTableQuery(Class<?> clazz) {

        return createDropTableQuery(getTableName(clazz));
    }

    private String createDropTableQuery(String tableName) {

        return String.format("DROP TABLE %s", tableName);
    }

    private String getTableName(Class<?> clazz) {

        return ClsssMetadataInspector.getTableName(clazz);
    }

    private String createColumnClause(Class<?> clazz) {
        List<String> columnClauses = ClsssMetadataInspector.getFields(clazz).stream()
                .map(this::createColumnClause)
                .collect(Collectors.toList());

        return String.join(", ", columnClauses);
    }

    private String createColumnClause(Field field) {
        String columnTypeFormat = "%s %s %s";

        return String.format(columnTypeFormat,
                EntityInfoExtractor.getColumnName(field),
                getColumnDataType(field),
                getColumnProperty(field)
        );
    }

    // TODO getColumnType Refactoring
    private String getColumnDataType(Field field) {
        String columnType = dialect.getColumnType(field.getType());
        int length = 0;
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            if (column.columnDefinition().length() > 0) {
                length = column.columnDefinition().length();
            }
        }

        if ("VARCHAR".equals(columnType) && length == 0) {
            length = 255;
        }

        if (length > 0) {
            return columnType + "(" + length + ")";
        }

        return columnType;
    }


    private String createPrimaryKeyClause(Class<?> clazz) {
        final String primaryKeyClauseFormat = ", PRIMARY KEY (%s)";

        return String.format(primaryKeyClauseFormat, getPrimaryKeyColumnName(clazz));
    }

    private String getPrimaryKeyColumnName(Class<?> clazz) {

        return EntityInfoExtractor.getIdColumnName(clazz);
    }

    private String getColumnProperty(Field field) {
        StringBuilder columnProperty = new StringBuilder();
        if (!EntityFieldInspector.isNullable(field)) {
            columnProperty.append("NOT NULL ");
        }
        if (EntityFieldInspector.isAutoIncrement(field)) {
            columnProperty.append("AUTO_INCREMENT ");
        }
        return columnProperty.toString();
    }
}
