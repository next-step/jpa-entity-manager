package persistence.entity.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.EntityStatus;
import persistence.entity.PersistenceContext;
import persistence.exception.NotExistException;

public class DefaultPersistenceContext implements PersistenceContext {

    private final Map<EntityKey, EntityEntry> context = new HashMap<>();

    @Override
    public <T, ID> EntityEntry getEntity(ID id, Class<T> entityType) {
        EntityKey key = new EntityKey(id, entityType);
        EntityEntry entry = context.get(key);
        validateEntryExist(key, entry);
        return entry;
    }

    @Override
    public <T> void addEntity(T entity, EntityStatus status) {
        EntityKey key = new EntityKey(entity);
        if (context.containsKey(key)) {
            return;
        }
        context.put(key, new EntityEntry(key, status, entity));
    }

    @Override
    public <T, ID> EntityEntry addLoadingEntity(ID id, Class<T> entityType) {
        EntityKey key = new EntityKey(id, entityType);
        if (context.containsKey(key)) {
            return context.get(key);
        }

        EntityEntry entry = EntityEntry.newLoadingEntity(key);
        context.put(key, entry);

        return entry;
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey key = new EntityKey(entity);
        EntityEntry entry = context.get(key);
        validateEntryExist(key, entry);
        entry.updateStatus(EntityStatus.DELETED);
        context.remove(key);
    }

    private void validateEntryExist(EntityKey key, EntityEntry entry) {
        if (entry == null) {
            throw new NotExistException(MessageFormat.format("EntityEntry EntityKey: {0}, EntityType: {1}", key.key(), key.entityType()));
        }
    }

}
