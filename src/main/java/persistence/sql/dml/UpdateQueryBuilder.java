package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {
    private static final String QUERY_TEMPLATE = "UPDATE %s SET %s WHERE %s";

    public String update(EntityTable entityTable, List<EntityColumn> dirtiedEntityColumns) {
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getSetClause(dirtiedEntityColumns),
                entityTable.getWhereClause());
    }

    private String getSetClause(List<EntityColumn> dirtiedEntityColumns) {
        final List<String> columnDefinitions = dirtiedEntityColumns.stream()
                .map(this::getSetClause)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private String getSetClause(EntityColumn entityColumn) {
        return entityColumn.getColumnName() + " = " + entityColumn.getValueWithQuotes();
    }
}
