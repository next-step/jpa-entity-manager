package persistence.sql.meta;

import jakarta.persistence.Entity;

import java.util.List;

public class EntityTable {
    public static final String NOT_ENTITY_FAILED_MESSAGE = "클래스에 @Entity 애노테이션이 없습니다.";
    public static final String NOT_ID_FAILED_MESSAGE = "필드에 @Id 애노테이션이 없습니다.";

    private final TableName tableName;
    private final EntityFields entityFields;

    public EntityTable(Class<?> entityType) {
        validate(entityType);
        this.tableName = new TableName(entityType);
        this.entityFields = new EntityFields(entityType);
    }

    public EntityTable(Object entity) {
        validate(entity.getClass());
        this.tableName = new TableName(entity.getClass());
        this.entityFields = new EntityFields(entity);
    }

    public String getTableName() {
        return tableName.getName();
    }

    public List<EntityColumn> getEntityFields() {
        return entityFields.getEntityFields();
    }

    public String getWhereClause(Object id) {
        final EntityColumn entityColumn = getIdEntityField();
        return entityColumn.getColumnName() + " = " + id;
    }

    public Object getIdValue() {
        final EntityColumn entityColumn = getIdEntityField();
        return entityColumn.getValue();
    }

    private void validate(Class<?> entityType) {
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(NOT_ENTITY_FAILED_MESSAGE);
        }
    }

    private EntityColumn getIdEntityField() {
        return entityFields.getIdEntityField();
    }
}
