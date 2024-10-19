package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateQueryBuilder {
    private static final String QUERY_TEMPLATE = "UPDATE %s SET %s WHERE %s";

    private final EntityTable entityTable;

    public UpdateQueryBuilder(Object entity) {
        this.entityTable = new EntityTable(entity);
    }

    public String update() {
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getSetClause(), getWhereClause());
    }

    private String getSetClause() {
        final List<String> columnDefinitions = entityTable.getEntityFields()
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
        return entityColumn.getColumnName() + " = " + entityColumn.getValue();
    }

    private String getWhereClause() {
        final Object id = entityTable.getIdValue();
        return entityTable.getWhereClause(id);
    }
}
