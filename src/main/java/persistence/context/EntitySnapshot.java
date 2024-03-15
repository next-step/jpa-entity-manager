package persistence.context;

import persistence.sql.metadata.ColumnMetadata;
import persistence.sql.metadata.EntityMetadata;

import java.util.Map;
import java.util.stream.Collectors;

public class EntitySnapshot {
    private final Map<ColumnMetadata, Object> snapShotColumns;

    private EntitySnapshot(Map<ColumnMetadata, Object> snapShotColumns) {
        this.snapShotColumns = snapShotColumns;
    }

    public static EntitySnapshot from(Object entity) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass(), entity);
        return new EntitySnapshot(entityMetadata.getColumns().stream()
                .collect(Collectors.toMap(column -> column, ColumnMetadata::getValue)));
    }

    public boolean isNotEqualToSnapshot(Object entity) {
        Map<ColumnMetadata, Object> entityState = EntityMetadata.of(entity.getClass(), entity).getColumns().stream()
                .collect(Collectors.toMap(column -> column, ColumnMetadata::getValue));

        if (entityState.size() != snapShotColumns.size()) {
            return true;
        }

        return snapShotColumns.entrySet().stream()
                .allMatch(entry -> entry.getValue().equals(entityState.get(entry.getKey())));
    }
}
