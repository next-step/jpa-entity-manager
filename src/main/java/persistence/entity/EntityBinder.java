package persistence.entity;

import persistence.sql.model.BaseColumn;
import persistence.sql.model.PKColumn;
import persistence.sql.model.Table;

import java.lang.reflect.Field;

public class EntityBinder {

    private final Object entity;
    private final Table table;

    public EntityBinder(Object entity) {
        this.entity = entity;
        this.table = buildTable(entity);
    }

    private Table buildTable(Object entity) {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;
        Class<?> clazz = entity.getClass();
        return entityMetaCache.getTable(clazz);
    }

    public EntityId getEntityId() {
        PKColumn pkColumn = table.getPKColumn();
        Object idValue = getValue(pkColumn);
        return new EntityId(idValue);
    }

    public void bindEntityId(EntityId id) {
        PKColumn pkColumn = table.getPKColumn();
        Object idValue = id.value();
        bindValue(pkColumn, idValue);
    }

    public void bindValue(BaseColumn column, Object value) {
        try {
            Field field = column.getField();
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(BaseColumn column) {
        try {
            Field field = column.getField();
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
