package persistence.core;

import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.EntityMetadata;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Snapshot {

    private Map<String, Object> snapshot;
    private EntityMetaManager entityMetaManager;

    public Snapshot(Object entity) {
        entityMetaManager = EntityMetaManager.getInstance();
        createEntitySnapshot(entity);
    }

    public Map<String, Object> getSnapshot() {
        return snapshot;
    }

    public Map<String, Object> createEntitySnapshot(Object entity) {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(entity.getClass());

        return entityMetadata.getColumns().getColumns().stream()
                .collect(Collectors.toMap(EntityColumn::getColumnName, column -> column.getValue(entity)));
    }



}
