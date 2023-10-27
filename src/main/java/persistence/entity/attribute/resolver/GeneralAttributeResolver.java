package persistence.entity.attribute.resolver;

import persistence.entity.attribute.GeneralAttribute;

import java.lang.reflect.Field;

public interface GeneralAttributeResolver {
    boolean support(Class<?> clazz);

    GeneralAttribute resolve(Field field);
}
