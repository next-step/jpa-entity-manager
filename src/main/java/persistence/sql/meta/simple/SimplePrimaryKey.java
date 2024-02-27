package persistence.sql.meta.simple;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import persistence.sql.meta.PrimaryKey;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SimplePrimaryKey implements PrimaryKey {

    private SimpleColumn primaryKey;

    private SimplePrimaryKey(final Class<?> clazz) {
        this.primaryKey = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isIdField)
                .findFirst()
                .map(SimpleColumn::new)
                .orElseThrow(IllegalArgumentException::new);
    }

    public static SimplePrimaryKey of(Class<?> clazz) {
        return new SimplePrimaryKey(clazz);
    }

    @Override
    public String name() {
        return this.primaryKey.getFieldName();
    }

    @Override
    public String value(final Object object) {
        return this.primaryKey.value(object);
    }

    @Override
    public Class<?> type() {
        return this.primaryKey.type();
    }

    @Override
    public GenerationType strategy() {
        return this.primaryKey.generateType();
    }

    private boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }
}
