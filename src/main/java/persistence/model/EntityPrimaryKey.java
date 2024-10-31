package persistence.model;

import jakarta.persistence.Id;
import jakarta.persistence.Table;
import persistence.util.ReflectionUtil;

import java.util.Map;

public record EntityPrimaryKey(String keyName, Object keyValue, String entityTableName) {
    public static EntityPrimaryKey build(Object entityObject) {
        Class<?> entityClass = entityObject.getClass();

        Map.Entry<String, Object> keyInfo = ReflectionUtil.getFieldNameAndValue(entityObject, Id.class);
        String entityTableName = ReflectionUtil.getClassName(entityClass, Table.class);

        return new EntityPrimaryKey(keyInfo.getKey(), keyInfo.getValue(), entityTableName);
    }

    public static EntityPrimaryKey build(Class<?> entityClass, Object pkValue) {
        String pkName = ReflectionUtil.getFieldName(entityClass, Id.class);
        String entityTableName = ReflectionUtil.getClassName(entityClass, Table.class);

        return new EntityPrimaryKey(pkName, pkValue, entityTableName);
    }

    public boolean isValid() {
        return keyValue != null;
    }
}
