package persistence.sql.meta;

import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityPrimaryKey {
    private List<EntityColumn> entityColumns;
    private EntityPrimaryKey(final Class<?> clazz) {
        this.entityColumns = Arrays.stream(clazz.getDeclaredFields())
                .filter(this::isNotTransientField)
                .filter(this::isIdField)
                .map(EntityColumn::new)
                .collect(Collectors.toList());
    }
    public static EntityPrimaryKey of(Class<?> clazz) {
        return new EntityPrimaryKey(clazz);
    }

    // 현재는 PK가 한개만 있다고 가정
    public String name() {
        return this.entityColumns.get(0).getFieldName();
    }

    public String value(final Object object) {
        return this.entityColumns.get(0).value(object);
    }

    public EntityColumn getEntityColumn() {
        return entityColumns.get(0);
    }

    public Class<?> type() {
        return this.entityColumns.get(0).type();
    }
    public GenerationType strategy() {
        return this.entityColumns.get(0).generateType();
    }

    private boolean isIdField(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private boolean isNotTransientField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }
}
