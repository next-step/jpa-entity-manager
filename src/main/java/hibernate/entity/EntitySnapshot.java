package hibernate.entity;

import hibernate.entity.column.EntityColumn;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EntitySnapshot {

    private final Map<EntityColumn, Object> snapshot;

    public EntitySnapshot(final Object snapshot) {
        this.snapshot = parseToSnapshot(snapshot);
    }

    private Map<EntityColumn, Object> parseToSnapshot(final Object entity) {
        return EntityClass.getInstance(entity.getClass())
                .getEntityColumns()
                .stream()
                .collect(Collectors.toMap(
                        entityColumn -> entityColumn,
                        entityColumn -> entityColumn.getFieldValue(entity),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));
    }

    public Map<EntityColumn, Object> getSnapshot() {
        return Collections.unmodifiableMap(snapshot);
    }
}
