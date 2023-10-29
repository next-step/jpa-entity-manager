package persistence.entity.attribute.resolver;

import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.attribute.id.StringTypeIdAttribute;

import java.lang.reflect.Field;

public class StringTypeIdAttributeResolver implements IdAttributeResolver {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public IdAttribute resolve(Field field) {
        return new StringTypeIdAttribute(field);
    }

    @Override
    public <T> void setIdToEntity(T instance, Field idField, long idValue) {
        throw new UnsupportedOperationException("String 타입에서는 지원하지않습니다");
    }
}
