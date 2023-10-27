package persistence.entity.attribute.resolver;

import persistence.entity.attribute.LongTypeGeneralAttribute;
import persistence.entity.attribute.id.IdAttribute;

import java.lang.reflect.Field;

public class LongTypeGeneralAttributeResolver implements GeneralAttributeResolver {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public IdAttribute resolve(Field field) {
        return LongTypeGeneralAttribute.of(field);
    }
}
