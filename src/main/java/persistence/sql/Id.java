package persistence.sql;

import java.lang.reflect.Field;

import static persistence.sql.EntityUtils.getColumnName;

public class Id {
    private final String name;
    private final Object value;

    public Id(String name, Object value) {
        try {
            this.name = name;
            this.value = value;
        } catch (Exception e) {
            throw new IllegalArgumentException("No id column found");
        }
    }

    public <T> Id(Class<T> clazz, Object value) {
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                this.name = getColumnName(field);
                this.value = value;
                return;
            }
        }
        throw new IllegalArgumentException("No id column found");
    }

    public <T> Id(Class<T> clazz) {
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                this.name = getColumnName(field);
                this.value = null;
                return;
            }
        }
        throw new IllegalArgumentException("No id column found");
    }

    public <T> Id(T instance) {
        final Class<?> clazz = instance.getClass();
        final Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                this.name = getColumnName(field);
                this.value = getValue(instance, field);
                return;
            }
        }
        throw new IllegalArgumentException("No id column found");
    }

    private <T> Object getValue(T entity, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Id id = (Id) o;

        if (name != null ? !name.equals(id.name) : id.name != null) return false;
        return value != null ? value.equals(id.value) : id.value == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
