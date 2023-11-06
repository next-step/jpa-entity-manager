package persistence.entity.impl.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import persistence.entity.impl.EntityIdentifier;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityClassMappingMeta;
import persistence.sql.schema.EntityObjectMappingMeta;

public class PersistenceCacheContext {

    private final ColumnType columnType;
    private final Map<EntityIdentifier, Object> contextCacheMap;

    private PersistenceCacheContext(ColumnType columnType, Map<EntityIdentifier, Object> contextCacheMap) {
        this.columnType = columnType;
        this.contextCacheMap = contextCacheMap;
    }

    public static PersistenceCacheContext of(ColumnType columnType) {
        return new PersistenceCacheContext(columnType, new HashMap<>());
    }

    public Optional<Object> tryGetEntityCache(Class<?> entityClazz, Object id) {
        final EntityClassMappingMeta classMappingMeta = EntityClassMappingMeta.of(entityClazz, columnType);
        final EntityIdentifier identifier = EntityIdentifier.fromIdColumnMetaWithValue(classMappingMeta.getIdColumnMeta(), id);
        return Optional.ofNullable(contextCacheMap.get(identifier));
    }

    public void putEntityCache(Object entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);
        EntityIdentifier identifier = objectMappingMeta.getEntityIdentifier();
        contextCacheMap.put(identifier, entity);
    }

    public void purgeEntityCache(Object entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);
        EntityIdentifier identifier = objectMappingMeta.getEntityIdentifier();

        contextCacheMap.remove(identifier);
    }

    public void clear() {
        contextCacheMap.clear();
    }
}
