package persistence.model;

import jakarta.persistence.Transient;
import persistence.sql.QueryException;

import java.lang.reflect.Field;
import java.util.*;

public class EntityMetaData {

    private final String entityName;
    private final Class<?> entityClass;
    private final Map<String, Field> fields = new LinkedHashMap<>();

    public EntityMetaData(final Class<?> entityClass) {
        this.entityName = entityClass.getName();
        this.entityClass = entityClass;
        extractFields(entityClass);
    }

    private void extractFields(final Class<?> entityClass) {
        Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .forEach(field -> this.fields.put(field.getName(), field));
    }

    public String getEntityName() {
        return this.entityName;
    }

    public Class<?> getEntityType() {
        return this.entityClass;
    }

    public List<Field> getFields() {
        return new ArrayList<>(this.fields.values());
    }

    public Map<String, Object> extractValues(final Object entity) {
        if (!entity.getClass().equals(entityClass)) {
            throw new MetaDataModelMappingException("not equal class type - meta data type: " + entityName + ", parameter type: " + entity.getClass().getName());
        }

        final Map<String, Object> values = new HashMap<>();

        fields.forEach((fieldName, field) -> {
            try {
                field.setAccessible(true);
                values.put(fieldName, field.get(entity));
            } catch (IllegalAccessException e) {
                throw new QueryException("can't get field " + field.getName() + " at " + entity.getClass().getName());
            } finally {
                field.setAccessible(false);
            }
        });

        return values;
    }
}
