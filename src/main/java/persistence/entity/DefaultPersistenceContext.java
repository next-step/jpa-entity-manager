package persistence.entity;

import persistence.sql.meta.EntityKey;
import persistence.sql.meta.EntityTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> entityRegistry = new ConcurrentHashMap<>();

    @Override
    public void addEntity(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        entityRegistry.put(entityTable.toEntityKey(), entity);
    }

    @Override
    public <T> T getEntity(Class<T> entityType, Object id) {
        final EntityKey entityKey = new EntityKey(entityType, id);
        return entityType.cast(entityRegistry.get(entityKey));
    }

    @Override
    public void removeEntity(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        entityRegistry.remove(entityTable.toEntityKey());
    }
}
