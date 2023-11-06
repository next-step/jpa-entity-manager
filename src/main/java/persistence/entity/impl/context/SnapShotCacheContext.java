package persistence.entity.impl.context;

import java.util.HashMap;
import java.util.Map;
import persistence.entity.impl.EntityIdentifier;
import persistence.entity.impl.SnapShot;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityClassMappingMeta;
import persistence.sql.schema.EntityObjectMappingMeta;

public class SnapShotCacheContext {
    private final Map<EntityIdentifier, Object> contextSnapshotCacheMap;
    private final ColumnType columnType;

    private SnapShotCacheContext(Map<EntityIdentifier, Object> contextSnapshotCacheMap, ColumnType columnType) {
        this.contextSnapshotCacheMap = contextSnapshotCacheMap;
        this.columnType = columnType;
    }

    public static SnapShotCacheContext of(ColumnType columnType) {
        return new SnapShotCacheContext(new HashMap<>(), columnType);
    }

    public Object putSnapShotCache(Object id, Object entity) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(entity.getClass(), columnType);

        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(classMappingMeta.getIdColumnMeta(), id);
        return contextSnapshotCacheMap.put(identifier, entity);
    }

    public SnapShot getSnapShotCache(Class<?> clazz, Object id) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(clazz, columnType);
        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(classMappingMeta.getIdColumnMeta(), id);

        return new SnapShot(contextSnapshotCacheMap.get(identifier), columnType);
    }

    public void purgeSnapShotCache(Object entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);
        EntityIdentifier identifier = objectMappingMeta.getEntityIdentifier();

        contextSnapshotCacheMap.remove(identifier);
    }

    public void clear() {
        contextSnapshotCacheMap.clear();
    }


}
