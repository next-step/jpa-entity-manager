package persistence.sql.meta;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityFields {
    public static final String NOT_ID_FAILED_MESSAGE = "필드에 @Id 애노테이션이 없습니다.";

    private final List<EntityField> entityFields;

    public EntityFields(Class<?> entityType) {
        this.entityFields = Arrays.stream(entityType.getDeclaredFields())
                .filter(this::isPersistent)
                .map(EntityField::new)
                .collect(Collectors.toList());
    }

    public EntityFields(Object entity) {
        this.entityFields = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(this::isPersistent)
                .map(field -> new EntityField(field, entity))
                .collect(Collectors.toList());
    }

    public List<EntityField> getEntityFields() {
        return entityFields;
    }

    public EntityField getIdEntityField() {
        return entityFields.stream()
                .filter(EntityField::isId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NOT_ID_FAILED_MESSAGE));
    }

    private boolean isPersistent(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }
}
