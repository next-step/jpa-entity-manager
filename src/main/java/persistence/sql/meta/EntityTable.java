package persistence.sql.meta;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;
import java.util.Objects;

public class EntityTable {
    public static final String NOT_ENTITY_FAILED_MESSAGE = "클래스에 @Entity 애노테이션이 없습니다.";
    public static final String NOT_ID_FAILED_MESSAGE = "필드에 @Id 애노테이션이 없습니다.";

    private final TableName tableName;
    private final EntityFields entityFields;

    public EntityTable(Class<?> entityType) {
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(NOT_ENTITY_FAILED_MESSAGE);
        }

        this.tableName = new TableName(entityType);
        this.entityFields = new EntityFields(entityType);
    }

    public String getTableName() {
        return tableName.getName();
    }

    public List<EntityField> getEntityFields() {
        return entityFields.getEntityFields();
    }


    public String getWhereClause(Object id) {
        final EntityField entityField = getIdEntityField();
        return entityField.getColumnName() + " = " + id;
    }

    public Object getIdValue(Object entity) {
        final EntityField entityField = getIdEntityField();
        return entityField.getValue(entity);
    }

    private EntityField getIdEntityField() {
        return entityFields.getIdEntityField();
    }
}
