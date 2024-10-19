package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class InsertQueryBuilder {
    private static final String QUERY_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";

    private final EntityTable entityTable;

    public InsertQueryBuilder(Object entity) {
        this.entityTable = new EntityTable(entity);
    }

    public String insert() {
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getColumnClause(), getValueClause());
    }

    private String getColumnClause() {
        final List<String> columnDefinitions = entityTable.getEntityFields()
                .stream()
                .filter(this::isNotNeeded)
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private String getValueClause() {
        final List<String> columnDefinitions = entityTable.getEntityFields()
                .stream()
                .filter(this::isNotNeeded)
                .map(EntityColumn::getValue)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private boolean isNotNeeded(EntityColumn entityColumn) {
        return !entityColumn.isGenerationValue();
    }
}
