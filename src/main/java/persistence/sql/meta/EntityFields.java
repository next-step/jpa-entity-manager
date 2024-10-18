package persistence.sql.meta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityFields {
    public static final String NOT_ID_FAILED_MESSAGE = "필드에 @Id 애노테이션이 없습니다.";

    private final List<EntityField> entityFields;

    public EntityFields(Class<?> entityType) {
        this.entityFields = Arrays.stream(entityType.getDeclaredFields())
                .map(EntityField::new)
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
}
