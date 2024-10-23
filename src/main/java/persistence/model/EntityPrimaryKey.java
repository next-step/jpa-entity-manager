package persistence.model;

import jakarta.persistence.Id;
import persistence.model.util.ReflectionUtil;

import java.util.Map;

public record EntityPrimaryKey(String keyName, Object keyValue, String entityTableName) {

    public static EntityPrimaryKey build(Object entityObject) {
        Class<?> entityClass = entityObject.getClass();

        Map.Entry<String, Object> keyInfo = ReflectionUtil.getFieldNameAndValue(entityObject, Id.class);
        String entityTableName = ReflectionUtil.getTableName(entityClass);

        return new EntityPrimaryKey(keyInfo.getKey(), keyInfo.getValue(), entityTableName);
    }

    public static EntityPrimaryKey build(Class<?> entityClass, Object pkValue) {
        String pkName = ReflectionUtil.getFieldName(entityClass, Id.class);
        String entityTableName = ReflectionUtil.getTableName(entityClass);

        return new EntityPrimaryKey(pkName, pkValue, entityTableName);
    }

    @Override
    public boolean equals(Object comparingObject) {
        if (this == comparingObject) {
            return true;
        }
        if (comparingObject == null || getClass() != comparingObject.getClass()) {
            return false;
        }

        EntityPrimaryKey that = (EntityPrimaryKey) comparingObject;

        return that.keyName.equals(keyName)
                && that.entityTableName.equals(entityTableName)
                && that.keyValue.equals(keyValue);
    }

}
