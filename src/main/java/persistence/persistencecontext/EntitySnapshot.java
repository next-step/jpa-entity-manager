package persistence.persistencecontext;

import persistence.sql.domain.Column;
import persistence.sql.domain.Table;
import utils.ValueExtractor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntitySnapshot {
    private final Map<Column, Object> snapshot;

    public EntitySnapshot(Map<Column, Object> snapshot) {
        this.snapshot = snapshot;
    }

    public static EntitySnapshot from(Object entity) {
        Table table = Table.from(entity.getClass());
        List<Column> columns = table.getColumns();
        Map<Column, Object> snapshot = columns.stream()
                .collect(LinkedHashMap::new,
                        (map, column) -> map.put(column, ValueExtractor.extract(entity, column)),
                        Map::putAll);
        return new EntitySnapshot(snapshot);
    }

    public boolean isChanged(Object entity) {
        return snapshot.keySet().stream()
                .anyMatch(column -> {
                    Object entityValue = ValueExtractor.extract(entity, column);
                    Object snapshotValue = snapshot.get(column);
                    return !Objects.equals(entityValue, snapshotValue);
                });
    }

    public Map<Column, Object> getSnapshot() {
        return snapshot;
    }
}
