package persistence.sql.meta;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityPrimaryKey {
    private EntityColumn primaryKey;
    private EntityPrimaryKey(final Class<?> clazz) {
        this.primaryKey = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isIdField)
                .findFirst()
                .map(EntityColumn::new)
                .orElseThrow(IllegalArgumentException::new);
    }
    public static EntityPrimaryKey of(Class<?> clazz) {
        return new EntityPrimaryKey(clazz);
    }

    public String name() {
        return this.primaryKey.getFieldName();
    }

    public String value(final Object object) {
        return this.primaryKey.value(object);
    }

    public Class<?> type() {
        return this.primaryKey.type();
    }

    public GenerationType strategy() {
        return this.primaryKey.generateType();
    }

    private boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }
}
