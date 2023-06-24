package persistence.entity;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityFields {
    private final List<EntityField> fields;

    public EntityFields(List<EntityField> fields) {
        this.fields = fields;
    }

    public static EntityFields of(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<EntityField> fields = Arrays.stream(declaredFields)
                .map(EntityField::new)
                .collect(Collectors.toList());

        return new EntityFields(fields);
    }

    public EntityField getId() {
        return fields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("@Id 어노테이션이 선언된 필드가 존재하지 않습니다."));
    }
}
