package persistence.sql;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Entity {
    protected static final String SINGLE_QUOTE = "'";
    private final Object entity;
    private final String name;
    private final Id id;
    private final Columns columns;

    public Entity(Object entity) {
        if (entity == null) {
            throw new IllegalStateException("Entity is not set");
        }
        this.entity = entity;
        final Class<?> clazz = entity.getClass();
        this.name = EntityUtils.getName(clazz);
        this.id = new Id(entity.getClass());
        this.columns = new Columns(clazz);
    }

    public Map<String, String> valuesByField() {
        Map<String, String> values = new LinkedHashMap<>();

        columns.getColumnValues().forEach(e -> values.put(e.getName(), convertValue(entity, e)));
        return values;
    }

    public List<String> getValues() {
        return valuesByField()
                .values()
                .stream()
                .toList();
    }

    private String convertValue(Object entity, Field field) {
        try {
            field.setAccessible(true);
            final Object value = field.get(entity);
            if (value == null) {
                return null;
            }
            if (field.getType().equals(String.class)) {
                return SINGLE_QUOTE + value + SINGLE_QUOTE;
            }
            return value.toString();
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot get value from field " + field.getName());
        }
    }

    public Id getId() {
        return id;
    }
}
