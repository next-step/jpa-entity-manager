package persistence.entity;

import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntitySnapshot {
    private final Map<String, Object> columnSnapshots = new HashMap<>();

    public EntitySnapshot(Object entity) {

        final TableDefinition tableDefinition = new TableDefinition(entity.getClass());
        final List<? extends Queryable> columns = tableDefinition.withoutIdColumns();
        for (Queryable column : columns) {
            columnSnapshots.put(column.getName(), column.hasValue(entity) ? column.getValue(entity) : null);
        }
    }
}
