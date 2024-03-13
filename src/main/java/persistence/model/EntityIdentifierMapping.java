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
    }

    public Object getIdentifier(final Object entity) {
        try {
            this.field.setAccessible(true);
            return this.field.get(entity);
        } catch (IllegalAccessException e) {
            throw new MetaDataModelMappingException(
                    "Error accessing field " + field.toGenericString() +
                            " by reflection for persistent property " + containerClass.getName() +
                            " : " + propertyName);
        } finally {
            this.field.setAccessible(false);
        }
    }

    public void setIdentifierValue(final Object entity, final Object value) {
        try {
            this.field.setAccessible(true);
            this.field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new MetaDataModelMappingException(
                    "Error accessing field " + field.toGenericString() +
                            " by reflection for persistent property " + containerClass.getName() +
                            " : " + propertyName);
        } finally {
            this.field.setAccessible(false);
        }
    }

}
