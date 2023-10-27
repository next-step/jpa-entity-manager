package persistence.entity.attribute.resolver;

import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.attribute.id.LongTypeIdAttribute;

import java.lang.reflect.Field;

public class LongTypeIdAttributeResolver implements IdAttributeResolver {

    @Override
    public boolean support(Class<?> clazz) {
        return clazz == Long.class;
    }

    @Override
    public IdAttribute resolve(Field field) {
        return LongTypeIdAttribute.of(field);
    }

    @Override
    public <T> void setGeneratedIdToEntity(T instance, Field idField, long key) {
        idField.setAccessible(true);
        try {
            idField.set(instance, key);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Long 타입의 ID 필드에 키 값을 할당하는데 실패했습니다.", e);
        }
    }
}
