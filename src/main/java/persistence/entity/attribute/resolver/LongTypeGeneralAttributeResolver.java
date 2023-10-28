package persistence.entity.attribute.resolver;

import persistence.entity.attribute.GeneralAttribute;
import persistence.entity.attribute.LongTypeGeneralAttribute;

import java.lang.reflect.Field;

public class LongTypeGeneralAttributeResolver implements GeneralAttributeResolver {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == Long.class;
    }

    @Override
    public GeneralAttribute resolve(Field field) {
        return new LongTypeGeneralAttribute(field);
    }
}
