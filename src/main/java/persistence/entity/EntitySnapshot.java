package persistence.entity;

import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EntitySnapshot {
    private final TableDefinition tableDefinition;
    private final Map<String, Object> columnSnapshots = new HashMap<>();

    public EntitySnapshot(Object entity) {
        tableDefinition = new TableDefinition(entity.getClass());
        final List<? extends Queryable> columns = tableDefinition.withoutIdColumns();
        for (Queryable column : columns) {
            columnSnapshots.put(column.getColumnName(), getNullableValue(entity, column));
        }
    }

    private static Object getNullableValue(Object entity, Queryable column) {
        return column.hasValue(entity) ? column.getValueAsString(entity) : null;
    }

    public boolean hasDirtyColumns(EntitySnapshot entitySnapshot, Object managedEntity) {
        final List<? extends Queryable> columns = tableDefinition.withoutIdColumns();
        return columns.stream()
                .anyMatch(column -> {
                            final Object entityValue = getNullableValue(managedEntity, column);
                            final Object snapshotValue = entitySnapshot.columnSnapshots.get(column.getColumnName());
                            return !Objects.equals(entityValue, snapshotValue);
                        }
                );
    }
}
