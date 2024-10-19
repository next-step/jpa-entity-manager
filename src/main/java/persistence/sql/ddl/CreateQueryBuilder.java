package persistence.sql.ddl;

import persistence.dialect.Dialect;
import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;
import persistence.sql.meta.JavaTypeConvertor;

import java.util.List;
import java.util.stream.Collectors;

public class CreateQueryBuilder {
    private static final String QUERY_TEMPLATE = "CREATE TABLE %s (%s)";
    private static final String NOT_NULL_COLUMN_DEFINITION = "NOT NULL";
    private static final String GENERATION_COLUMN_DEFINITION = "AUTO_INCREMENT";
    private static final String PRIMARY_KEY_COLUMN_DEFINITION = "PRIMARY KEY";

    private final EntityTable entityTable;
    private final Dialect dialect;

    public CreateQueryBuilder(Class<?> entityType, Dialect dialect) {
        this.entityTable = new EntityTable(entityType);
        this.dialect = dialect;
    }

    public String create() {
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getColumnClause());
    }

    private String getColumnClause() {
        final List<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .map(this::getColumnDefinition)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private String getColumnDefinition(EntityColumn entityColumn) {
        String columDefinition = entityColumn.getColumnName() + " " + getDbType(entityColumn);

        if (entityColumn.isNotNull()) {
            columDefinition += " " + NOT_NULL_COLUMN_DEFINITION;
        }

        if (entityColumn.isGenerationValue()) {
            columDefinition += " " + GENERATION_COLUMN_DEFINITION;
        }

        if (entityColumn.isId()) {
            columDefinition += " " + PRIMARY_KEY_COLUMN_DEFINITION;
        }

        return columDefinition;
    }

    private String getDbType(EntityColumn entityColumn) {
        final int sqlType = new JavaTypeConvertor().getSqlType(entityColumn.getType());
        final String dbTypeName = dialect.getDbTypeName(sqlType);
        final int columnLength = entityColumn.getColumnLength();

        if (columnLength == 0) {
            return dbTypeName;
        }
        return "%s(%s)".formatted(dbTypeName, columnLength);
    }
}
