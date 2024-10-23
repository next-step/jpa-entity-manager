package persistence.sql.meta;

import jakarta.persistence.Entity;

import java.util.List;

public class EntityTable {
    public static final String NOT_ENTITY_FAILED_MESSAGE = "클래스에 @Entity 애노테이션이 없습니다.";

    private final Class<?> type;
    private final TableName tableName;
    private final EntityColumns entityColumns;

    public EntityTable(Class<?> entityType) {
        validate(entityType);
        this.type = entityType;
        this.tableName = new TableName(entityType);
        this.entityColumns = new EntityColumns(entityType);
    }

    public EntityTable(Object entity) {
        validate(entity.getClass());
        this.type = entity.getClass();
        this.tableName = new TableName(entity.getClass());
        this.entityColumns = new EntityColumns(entity);
    }

    public String getTableName() {
        return tableName.value();
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns.getEntityColumns();
    }

    public String getWhereClause() {
        final EntityColumn entityColumn = entityColumns.getIdEntityColumn();
        return entityColumn.getColumnName() + " = " + entityColumn.getValueWithQuotes();
    }

    public String getWhereClause(Object id) {
        final EntityColumn entityColumn = entityColumns.getIdEntityColumn();
        return entityColumn.getColumnName() + " = " + getValueWithQuotes(id);
    }

    public Object getIdValue() {
        final EntityColumn entityColumn = entityColumns.getIdEntityColumn();
        return entityColumn.getValue();
    }

    public String getIdValueWithQuotes() {
        final EntityColumn entityColumn = entityColumns.getIdEntityColumn();
        return entityColumn.getValueWithQuotes();
    }

    public EntityKey toEntityKey() {
        return new EntityKey(type, getIdValue());
    }

    private void validate(Class<?> entityType) {
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(NOT_ENTITY_FAILED_MESSAGE);
        }
    }

    public String getValueWithQuotes(Object id) {
        if (id.getClass() == String.class) {
            return "'%s'".formatted(String.valueOf(id));
        }
        return String.valueOf(id);
    }

    public int getColumnCount() {
        return getEntityColumns().size();
    }

    public EntityColumn getEntityColumn(int index) {
        return getEntityColumns().get(index);
    }
}
