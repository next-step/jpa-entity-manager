package persistence.entity.attribute.resolver;

import persistence.entity.attribute.id.IdAttribute;

import java.lang.reflect.Field;

public interface IdAttributeResolver {
    boolean supports(Class<?> clazz);

    IdAttribute resolve(Field field);

    <T> void setIdToEntity(T instance, Field idField, long idValue);
}
