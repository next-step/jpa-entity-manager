package persistence.sql;

import java.util.List;
import java.util.stream.Collectors;

public class EntityMetadata {
    private final Class<?> entityClass;
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;

    public EntityMetadata(Class<?> entityClass) {
        this(entityClass, new EntityTable(entityClass), new EntityColumns(entityClass));
    }

    public EntityMetadata(Class<?> entityClass, EntityTable entityTable, EntityColumns entityColumns) {
        this.entityClass = entityClass;
        this.entityTable = entityTable;
        this.entityColumns = entityColumns;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public EntityTable getEntityTable() {
        return entityTable;
    }

    public String getTableName() {
        return entityTable.getFullTableName();
    }

    public String getSchemaName() {
        return entityTable.getSchemaName();
    }

    public List<EntityColumnValue> getEntityColumnValuesFrom(Object entity) {
        return getEntityColumns().stream()
                .map(entityColumn -> entityColumn.getEntityColumnValueFrom(entity))
                .collect(Collectors.toList());
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

    public Object getIdFrom(Object entity) {
        EntityId entityId = getEntityIdFrom(entity);

        return entityId.getId();
    }

    public EntityColumn getEntityIdColumn() {
        return entityColumns.getEntityIdColumn();
    }

    public boolean hasIdFrom(Object entity) {
        return getIdFrom(entity) != null;
    }

    public Object getIdFrom(Class<?> entityClass, Object id) {
        EntityId entityId = getEntityIdFrom(entityClass, id);

        return entityId.getId();
    }
}
