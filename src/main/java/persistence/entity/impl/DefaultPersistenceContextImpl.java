package persistence.entity.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import persistence.entity.PersistenceContext;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityClassMappingMeta;
import persistence.sql.schema.EntityObjectMappingMeta;

public class DefaultPersistenceContextImpl implements PersistenceContext {

    private final Map<EntityIdentifier, Object> contextCacheMap = new HashMap<>();
    private final Map<EntityIdentifier, Object> contextSnapshotCacheMap = new HashMap<>();
    private final ColumnType columnType;

    public DefaultPersistenceContextImpl(ColumnType columnType) {
        this.columnType = columnType;
    }

    @Override
    public Optional<Object> getEntity(Class<?> entityClazz, Object id) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(entityClazz, columnType);
        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(classMappingMeta.getIdColumnMeta(), id);

        return Optional.ofNullable(contextCacheMap.get(identifier));
    }

    @Override
    public void addEntity(Object id, Object entity) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(entity.getClass(), columnType);
        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(classMappingMeta.getIdColumnMeta(), id);

        contextCacheMap.put(identifier, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);
        EntityIdentifier identifier = objectMappingMeta.getEntityIdentifier();

        contextCacheMap.remove(identifier);
    }

    @Override
    public Object getDatabaseSnapshot(Object id, Object entity) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(entity.getClass(), columnType);

        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(classMappingMeta.getIdColumnMeta(), id);

        return contextSnapshotCacheMap.put(identifier, entity);
    }

    @Override
    public void purgeEntityCache(Object entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);
        EntityIdentifier identifier = objectMappingMeta.getEntityIdentifier();

        contextSnapshotCacheMap.remove(identifier);
    }

    @Override
    public SnapShot getSnapShot(Class<?> entityClazz, Object id) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(entityClazz, columnType);
        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(classMappingMeta.getIdColumnMeta(), id);

        return new SnapShot(contextSnapshotCacheMap.get(identifier), columnType);
    }

    @Override
    public void clearContextCache() {
        contextCacheMap.clear();
        contextSnapshotCacheMap.clear();
    }


}
