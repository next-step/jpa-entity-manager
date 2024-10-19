package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class SelectQueryBuilder {
    private static final String FIND_ALL_QUERY_TEMPLATE = "SELECT %s FROM %s";
    private static final String FIND_BY_ID_QUERY_TEMPLATE = "SELECT %s FROM %s WHERE %s";

    private final EntityTable entityTable;

    public SelectQueryBuilder(Class<?> entityType) {
        this.entityTable = new EntityTable(entityType);
    }

    public String findAll() {
        return FIND_ALL_QUERY_TEMPLATE.formatted(getColumnClause(), entityTable.getTableName());
    }

    public String findById(Object id) {
        return FIND_BY_ID_QUERY_TEMPLATE.formatted(getColumnClause(), entityTable.getTableName(), entityTable.getWhereClause(id));
    }

    private String getColumnClause() {
        final List<String> columnDefinitions = entityTable.getEntityFields()
                .stream()
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }
}
