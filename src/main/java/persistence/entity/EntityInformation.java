package persistence.entity;

import persistence.sql.domain.DatabasePrimaryColumn;
import persistence.sql.domain.DatabaseTable;

import java.lang.reflect.Field;

public class EntityInformation {

    public boolean isNew(Object entity) {
        DatabasePrimaryColumn primaryColumn = new DatabaseTable(entity).getPrimaryColumn();
        return !primaryColumn.hasColumnValue();
    }

    public void setEntityId(Object entity, Long id) {
        DatabasePrimaryColumn primaryColumn = new DatabaseTable(entity).getPrimaryColumn();
        String javaFieldName = primaryColumn.getJavaFieldName();
        try {
            Field declaredField = entity.getClass().getDeclaredField(javaFieldName);
            declaredField.setAccessible(true);
            declaredField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
