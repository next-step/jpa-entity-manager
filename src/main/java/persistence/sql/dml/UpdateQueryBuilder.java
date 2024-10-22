package persistence.sql.dml;

import persistence.sql.meta.EntityColumn;
import persistence.sql.meta.EntityTable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UpdateQueryBuilder {
    public static final String NOT_CHANGED_MESSAGE = "변경된 필드가 없습니다.";
    private static final String QUERY_TEMPLATE = "UPDATE %s SET %s WHERE %s";

    public String update(Object entity, Object snapshot) {
        final EntityTable entityTable = new EntityTable(entity);
        final EntityTable snapshotEntityTable = new EntityTable(snapshot);
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getSetClause(entityTable, snapshotEntityTable),
                entityTable.getWhereClause());
    }

    private String getSetClause(EntityTable entityTable, EntityTable snapshotEntityTable) {
        final List<String> columnDefinitions = IntStream.range(0, entityTable.getColumnCount())
                .filter(i -> isDirtied(entityTable.getEntityColumn(i), snapshotEntityTable.getEntityColumn(i)))
                .mapToObj(entityTable::getEntityColumn)
                .map(this::getSetClause)
                .collect(Collectors.toList());

        if (columnDefinitions.isEmpty()) {
            throw new IllegalStateException(NOT_CHANGED_MESSAGE);
        }

        return String.join(", ", columnDefinitions);
    }

    private boolean isDirtied(EntityColumn entityColumn, EntityColumn snapshotEntityColumn) {
        return !Objects.equals(entityColumn.getValue(), snapshotEntityColumn.getValue());
    }

    private String getSetClause(EntityColumn entityColumn) {
        return entityColumn.getColumnName() + " = " + entityColumn.getValueWithQuotes();
    }
}
