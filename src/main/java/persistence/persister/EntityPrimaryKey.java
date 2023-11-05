package persistence.persister;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.TableSQLMapper;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityPrimaryKey {
    private final Field primaryKeyField;

    public EntityPrimaryKey(Class<?> clazz) {
        primaryKeyField = Arrays
            .stream(clazz.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .filter(field -> field.isAnnotationPresent(Id.class))
            .peek(field -> field.setAccessible(true))
            .findFirst()
            .orElse(null);
    }

    public String getPrimaryKeyName() {
        return TableSQLMapper.getColumnName(this.primaryKeyField);
    }

    public Object getPrimaryKeyValue(Object object) {
        return TableSQLMapper.getColumnValue(this.primaryKeyField, object);
    }

    public void setPrimaryKey(Object object, Object value) {
        try {
            primaryKeyField.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
