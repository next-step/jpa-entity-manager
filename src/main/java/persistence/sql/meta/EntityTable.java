package persistence.sql.meta;

import jakarta.persistence.Entity;

import java.util.List;

public class EntityTable {
    public static final String NOT_ENTITY_FAILED_MESSAGE = "클래스에 @Entity 애노테이션이 없습니다.";

    private final TableName tableName;
    private final EntityColumns entityColumns;

    public EntityTable(Class<?> entityType) {
        validate(entityType);
        this.tableName = new TableName(entityType);
        this.entityColumns = new EntityColumns(entityType);
    }

    public EntityTable(Object entity) {
        validate(entity.getClass());
        this.tableName = new TableName(entity.getClass());
        this.entityColumns = new EntityColumns(entity);
    }

    public String getTableName() {
        return tableName.getName();
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns.getEntityColumns();
    }

    public String getWhereClause(Object id) {
        final EntityColumn entityColumn = entityColumns.getIdEntityColumn();
        return entityColumn.getColumnName() + " = " + id;
    }

    public Object getIdValue() {
        final EntityColumn entityColumn = entityColumns.getIdEntityColumn();
        return entityColumn.getValue();
    }

    private void validate(Class<?> entityType) {
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(NOT_ENTITY_FAILED_MESSAGE);
        }
    }

}
