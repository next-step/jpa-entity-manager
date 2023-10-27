package persistence.entity.attribute.resolver;

import persistence.entity.attribute.StringTypeGeneralAttribute;
import persistence.entity.attribute.id.IdAttribute;

import java.lang.reflect.Field;

public class StringTypeGeneralAttributeResolver implements GeneralAttributeResolver {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public IdAttribute resolve(Field field) {
        return StringTypeGeneralAttribute.of(field);
    }
}
