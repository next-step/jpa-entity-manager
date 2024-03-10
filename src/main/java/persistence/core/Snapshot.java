package persistence.core;

import persistence.entity.metadata.EntityColumn;
import persistence.entity.metadata.EntityMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Snapshot {

    private Map<String, Object> snapshot;
    private EntityMetaManager entityMetaManager;

    public Snapshot(Object entity) {
        entityMetaManager = EntityMetaManager.getInstance();
        snapshot = createEntitySnapshot(entity);
    }

    public Map<String, Object> get() {
        return snapshot;
    }

    public Map<String, Object> createEntitySnapshot(Object entity) {
        snapshot = new HashMap<>();
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(entity.getClass());
        List<EntityColumn> columns = entityMetadata.getColumns().getColumns();
        for (EntityColumn column : columns) {
            snapshot.put(column.getColumnName(), column.getValue(entity));
        }

        return snapshot;
    }



}
