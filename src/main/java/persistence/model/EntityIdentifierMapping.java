package persistence.model;

import java.lang.reflect.Field;

public class EntityIdentifierMapping {
    private final Class<?> containerClass;
    private final String propertyName;
    private final Field field;

    public EntityIdentifierMapping(final Class<?> containerClass, final String propertyName, final Field field) {
        this.containerClass = containerClass;
        this.propertyName = propertyName;
        this.field = field;
        this.field.setAccessible(true);
    }

    public Object getIdentifier(Object entity) {
        try {
            return this.field.get(entity);
        } catch (IllegalAccessException e) {
            throw new MetaDataModelMappingException(
                    "Error accessing field " + field.toGenericString() +
                            " by reflection for persistent property " + containerClass.getName() +
                            " : " + propertyName);
        }
    }

}
