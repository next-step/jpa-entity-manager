package persistence.sql;

import java.util.List;
import java.util.stream.Collectors;

public class EntityMetadata {
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;

    public EntityMetadata(Class<?> entityClass) {
        this(new EntityTable(entityClass), new EntityColumns(entityClass));
    }

    public EntityMetadata(EntityTable entityTable, EntityColumns entityColumns) {
        this.entityTable = entityTable;
        this.entityColumns = entityColumns;
    }

    public String getTableName() {
        return entityTable.getFullTableName();
    }

    public String getSchemaName() {
        return entityTable.getSchemaName();
    }

    public EntityTable getEntityTable() {
        return entityTable;
    }

    public EntityColumnValues getEntityColumnValuesFrom(Object entity) {
        return new EntityColumnValues(
            getEntityColumns().stream()
                .map(entityColumn -> entityColumn.getEntityColumnValueFrom(entity))
                .collect(Collectors.toList())
        );
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns.getColumns();
    }

    public EntityId getEntityIdFrom(Object entity) {
        EntityColumn entityIdColumn = getEntityIdColumn();

        EntityColumnValue entityIdColumnValue = entityIdColumn.getEntityColumnValueFrom(entity);

        return new EntityId(entity.getClass(), entityIdColumnValue.getColumnValue());
    }

    public EntityId getEntityIdFrom(Class<?> entityClass, Object id) {
        return new EntityId(entityClass, id);
    }

    public EntityColumn getEntityIdColumn() {
        return entityColumns.getEntityIdColumn();
    }
}
