package hibernate.entity.persistencecontext;

import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityColumn;

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

    public Map<EntityColumn, Object> changedColumns(final Object entity) {
        return snapshot.entrySet()
                .stream()
                .filter(entry -> entry.getValue() != entry.getKey().getFieldValue(entity))
                .map(Map.Entry::getKey)
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
