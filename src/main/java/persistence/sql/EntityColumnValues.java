package persistence.sql;

import java.util.List;
import java.util.stream.Collectors;

public class EntityColumnValues {
    private final List<EntityColumnValue> container;

    public <T> EntityColumnValues(T entity) {
        this(
            EntityColumns.streamFrom(entity.getClass())
                .map(entityColumn -> entityColumn.getEntityValueFrom(entity))
                .collect(Collectors.toList())
        );
    }

    public EntityColumnValues(List<EntityColumnValue> container) {
        this.container = container;
    }

    public List<EntityColumnValue> getEntityColumnValues() {
        return container;
    }
}
