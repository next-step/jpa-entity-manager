package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class SelectQueryBuilder {
    private static final String FIND_ALL_QUERY_TEMPLATE = "SELECT %s FROM %s";
    private static final String FIND_BY_ID_QUERY_TEMPLATE = "SELECT %s FROM %s WHERE %s";

    public String findAll(Class<?> entityType) {
        final EntityTable entityTable = new EntityTable(entityType);
        return FIND_ALL_QUERY_TEMPLATE.formatted(getColumnClause(entityTable), entityTable.getTableName());
    }

    public String findById(Class<?> entityType, Object id) {
        final EntityTable entityTable = new EntityTable(entityType);
        return FIND_BY_ID_QUERY_TEMPLATE.formatted(getColumnClause(entityTable), entityTable.getTableName(),
                entityTable.getWhereClause(id));
    }

    private String getColumnClause(EntityTable entityTable) {
        final List<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }
}
