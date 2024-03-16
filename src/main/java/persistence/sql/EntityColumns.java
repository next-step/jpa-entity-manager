package persistence.sql;

import static persistence.sql.ddl.common.StringConstants.PRIMARY_KEY_NOT_FOUND;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityColumns {
    private final List<EntityColumn> container;

    public EntityColumns(Class<?> entityClass) {
        this(
            streamFrom(entityClass)
                .collect(Collectors.toList())
        );
    }

    public EntityColumns(List<EntityColumn> container) {
        this.container = container;
    }

    public EntityColumn getEntityIdColumn() {
        return container.stream()
            .filter(EntityColumn::isPrimary)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(PRIMARY_KEY_NOT_FOUND));
    }

    public List<EntityColumn> getColumns() {
        return container;
    }

    public static Stream<EntityColumn> streamFrom(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .sorted(Comparator.comparing(field -> field.isAnnotationPresent(Id.class) ? 0 : 1))
            .map(EntityColumn::new);
    }
}
