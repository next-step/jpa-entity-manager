package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {
    private static final String QUERY_TEMPLATE = "UPDATE %s SET %s WHERE %s";

    public String update(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getSetClause(entityTable), entityTable.getWhereClause());
    }

    private String getSetClause(EntityTable entityTable) {
        final List<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(this::getSetClause)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private boolean isNotNeeded(EntityColumn entityColumn) {
        return !entityColumn.isId();
    }

    private String getSetClause(EntityColumn entityColumn) {
        return entityColumn.getColumnName() + " = " + entityColumn.getValueWithQuotes();
    }
}
