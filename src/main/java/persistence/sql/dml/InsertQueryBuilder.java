package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class InsertQueryBuilder {
    private static final String QUERY_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";

    public String insert(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getColumnClause(entityTable), getValueClause(entityTable));
    }

    private String getColumnClause(EntityTable entityTable) {
        final List<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private String getValueClause(EntityTable entityTable) {
        final List<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(EntityColumn::getValueWithQuotes)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private boolean isNotNeeded(EntityColumn entityColumn) {
        return !entityColumn.isGenerationValue();
    }
}
