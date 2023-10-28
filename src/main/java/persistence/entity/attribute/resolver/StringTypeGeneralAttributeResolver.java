package persistence.entity.attribute.resolver;

import persistence.entity.attribute.GeneralAttribute;
import persistence.entity.attribute.StringTypeGeneralAttribute;

import java.lang.reflect.Field;

public class StringTypeGeneralAttributeResolver implements GeneralAttributeResolver {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == String.class;
    }

    @Override
    public GeneralAttribute resolve(Field field) {
        return new StringTypeGeneralAttribute(field);
    }
}
