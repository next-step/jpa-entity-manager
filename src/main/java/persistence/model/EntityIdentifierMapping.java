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

    public void setIdentifier(Object entity, Object id) {
        try {
            this.field.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new MetaDataModelMappingException("Null value was assigned to a property [" + containerClass + "." + propertyName + "] of primitive type");
        }
    }
}
