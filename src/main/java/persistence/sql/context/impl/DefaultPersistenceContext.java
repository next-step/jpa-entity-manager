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
        EntityEntry entityEntry = EntityEntry.newEntry(entity, status);
        context.put(entityEntry.getKey(), entityEntry);

        return entityEntry;
    }

    @Override
    public <T> EntityEntry addLoadingEntry(Object primaryKey, Class<T> returnType) {
        EntityEntry entityEntry = EntityEntry.newLoadingEntry(primaryKey, returnType);
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
    public void dirtyCheck(EntityPersister persister) {
        for (EntityEntry entry : context.values()) {
            handleEntry(persister, entry);
        }
    }

    private void handleEntry(EntityPersister persister, EntityEntry entry) {
        switch (entry.getStatus()) {
            case SAVING:
                handleSavingEntry(persister, entry);
                break;
            case MANAGED:
                handleUpdateEntry(persister, entry);
                break;
            case DELETED:
                handleDeleteEntry(persister, entry);
                break;
        }
    }

    private void handleSavingEntry(EntityPersister persister, EntityEntry entry) {
        persister.insert(entry.getEntity());
        entry.updateStatus(Status.MANAGED);
        entry.synchronizingSnapshot();
    }

    private void handleUpdateEntry(EntityPersister persister, EntityEntry entry) {
        if (!entry.isDirty()) {
            return;
        }
        persister.update(entry.getEntity(), entry.getSnapshot());
        entry.synchronizingSnapshot();
    }

    private void handleDeleteEntry(EntityPersister persister, EntityEntry entry) {
        persister.delete(entry.getEntity());
        entry.updateStatus(Status.GONE);
        context.remove(entry.getKey());
    }

    @Override
    public void cleanup() {
        context.clear();
    }
}
