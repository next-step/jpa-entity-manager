package persistence.sql.context.impl;

import persistence.sql.context.EntityPersister;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.entity.EntityEntry;
import persistence.sql.entity.data.Status;

import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<KeyHolder, EntityEntry> context = new HashMap<>();

    @Override
    public <T> EntityEntry addEntry(T entity, Status status, EntityPersister entityPersister) {
        EntityEntry entityEntry = EntityEntry.newEntry(entityPersister, this, entity, status);
        context.put(entityEntry.getKey(), entityEntry);

        return entityEntry;
    }

    @Override
    public <T> EntityEntry addEntry(Object primaryKey, Class<T> returnType, Status status, EntityPersister entityPersister) {
        EntityEntry entityEntry = EntityEntry.newLoadingEntry(entityPersister, this, primaryKey, returnType);
        context.put(entityEntry.getKey(), entityEntry);

        return entityEntry;
    }

    @Override
    public <T, ID> EntityEntry getEntry(Class<T> entityType, ID id) {
        KeyHolder key = new KeyHolder(entityType, id);

        if (context.containsKey(key)) {
            return context.get(key);
        }

        return null;
    }

    @Override
    public <T, ID> void deleteEntry(T entity, ID id) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        context.remove(key);
    }

    @Override
    public void dirtyCheck() {
        for (EntityEntry entry : context.values()) {
            entry.dirtyCheck();
        }
    }

    @Override
    public void cleanup() {
        context.clear();
    }
}
