package persistence.entity.attribute.resolver;

import persistence.entity.attribute.GeneralAttribute;

public interface GeneralAttributeResolver {
    boolean support(Class<?> clazz);

    GeneralAttribute resolver(Field field);
}
