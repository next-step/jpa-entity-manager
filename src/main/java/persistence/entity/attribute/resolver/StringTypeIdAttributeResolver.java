package persistence.entity.attribute.resolver;

import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.attribute.id.StringTypeIdAttribute;

import java.lang.reflect.Field;

public class StringTypeIdAttributeResolver implements IdAttributeResolver {

    @Override
    public boolean support(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public IdAttribute resolve(Field field) {
        return StringTypeIdAttribute.of(field);
    }

    @Override
    public <T> void setGeneratedIdToEntity(T instance, Field idField, long key) {
        throw new UnsupportedOperationException("String 타입에서는 지원하지않습니다");
    }
}
