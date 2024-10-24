package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {
    private static final String QUERY_TEMPLATE = "UPDATE %s SET %s WHERE %s";

    public String update(Object entity, List<EntityColumn> entityColumns) {
        final EntityTable entityTable = new EntityTable(entity);
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getSetClause(entityColumns),
                entityTable.getWhereClause());
    }

    private String getSetClause(List<EntityColumn> entityColumns) {
        final List<String> columnDefinitions = entityColumns.stream()
                .map(this::getSetClause)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private String getSetClause(EntityColumn entityColumn) {
        return entityColumn.getColumnName() + " = " + entityColumn.getValueWithQuotes();
    }
}
