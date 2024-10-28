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

    private final EntityPersister persister;

    public DefaultPersistenceContext(EntityPersister persister) {
        this.persister = persister;
    }

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
            handleEntry(entry);
        }
    }

    private void handleEntry(EntityEntry entry) {
        switch (entry.getStatus()) {
            case SAVING:
                handleSavingEntry(entry);
                break;
            case MANAGED:
                handleUpdateEntry(entry);
                break;
            case DELETED:
                handleDeleteEntry(entry);
                break;
        }
    }

    private void handleSavingEntry(EntityEntry entry) {
        persister.insert(entry.getEntity());
        entry.updateStatus(Status.MANAGED);
        entry.synchronizingSnapshot();
    }

    private void handleUpdateEntry(EntityEntry entry) {
        if (!entry.isDirty()) {
            return;
        }
        persister.update(entry.getEntity(), entry.getSnapshot());
        entry.synchronizingSnapshot();
    }

    private void handleDeleteEntry(EntityEntry entry) {
        persister.delete(entry.getEntity());
        entry.updateStatus(Status.GONE);
        context.remove(entry.getKey());
    }

    @Override
    public void cleanup() {
        context.clear();
    }
}
