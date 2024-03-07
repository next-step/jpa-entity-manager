package persistence.entity.domain;

import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.dml.domain.Value;

import java.util.LinkedHashMap;
import java.util.Map;

public class EntitySnapshot {

    private final Map<Column, Value> snapshotMap;

    public EntitySnapshot(Object entity) {
        Columns columns = new Columns(entity.getClass());
        snapshotMap = columns.getColumns().stream()
                .collect(LinkedHashMap::new,
                        (map, column) -> map.put(column, new Value(column, entity)),
                        LinkedHashMap::putAll);
    }

    public Map<Column, Value> getSnapshotMap() {
        return snapshotMap;
    }

}
