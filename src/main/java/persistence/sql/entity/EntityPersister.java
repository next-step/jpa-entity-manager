package persistence.sql.entity;

import jakarta.persistence.Id;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.lang.reflect.Field;

public class EntityPersister {

    private final String entityName;
    private final EntityTable entityTable;
    private final EntityColumns entityColumns;

    public EntityPersister(Class<?> clazz) {
        this.entityName = clazz.getSimpleName();
        this.entityTable = EntityTable.from(clazz);
        this.entityColumns = EntityColumns.from(clazz);

    }

    public String update(Object entity) {
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
        return updateQueryBuilder.update(entityTable, entityColumns, entity, getIdValue(entity));
    }

    public String insert(Object entity) {
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
        return insertQueryBuilder.getInsertQuery(entityTable, entityColumns, entity);
    }

    public String delete(Object entity) {
        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();
        return deleteQueryBuilder.delete(entityTable, entityColumns, getIdValue(entity));
    }

    public String select(Long id) {
        SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();
        return selectQueryBuilder.findById(entityTable, entityColumns, id);
    }

    public Long getIdValue(Object entity) {
        Class<?> clazz = entity.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                try {
                    return (Long) field.get(entity);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("id값이 없음");
                }
            }
        }
        return null;
    }

}
