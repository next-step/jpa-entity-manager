package persistence.entity;

import domain.Snapshot;
import persistence.exception.InvalidContextException;

import java.util.List;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    private final EntityEntry entityEntry;
    private final PersistenceContext persistenceContext;
    private final Map<String, EntityPersister<?>> persisterMap;

    EntityManagerImpl(Map<String, EntityPersister<?>> persisterMap) {
        this.entityEntry = new EntityEntry();
        this.persisterMap = persisterMap;
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> List<T> findAll(Class<T> tClazz) {
        return (List<T>) getPersister(tClazz).findAll();
    }

    @Override
    public <R, I> R find(Class<R> rClass, I input) {
        final EntityPersister<?> persister = getPersister(rClass);
        int key = persister.getHashCode(input);

        if (entityEntry.isManaged(key)) {
            return (R) persistenceContext.getEntity(key);
        }

        entityEntry.loading(key);
        persistenceContext.addEntity(key, persistenceContext.getDatabaseSnapshot(key, persister, input));
        entityEntry.managed(key);

        return (R) persistenceContext.getEntity(key);
    }

    @Override
    public <T> T persist(T t) {
        final EntityPersister<?> persister = getPersister(t.getClass());
        Object id = persister.getIdValue(t);
        int key = persister.getHashCode(id);

        persistenceContext.addEntity(key, id, persister.getEntity(t));
        entityEntry.saving(key);

        return (T) persistenceContext.getEntity(key);
    }

    @Override
    public <T> void remove(T t, Object arg) {
        final EntityPersister<?> persister = getPersister(t.getClass());
        int key = persister.getHashCode(arg);

        if (entityEntry.isGone(key)) {
            throw new InvalidContextException();
        }

        persistenceContext.removeEntity(key);
        entityEntry.deleted(key);
    }

    public void flush() {
        persistenceContext.comparison().forEach((id, data) -> {
            final EntityPersister<?> persister = getPersister(data.getObjectClass());

            if (!persistenceContext.isEntityInSnapshot(id)) {
                insert(persister, id, data);
                return;
            }

            if (!persistenceContext.isEntityInContext(id)) {
                delete(persister, id, data);
                return;
            }

            update(persister, id, data);
        });
    }

    private void insert(EntityPersister<?> persister, Integer id, Snapshot snapshot) {
        persister.insert(snapshot.getObject());
        persistenceContext.getDatabaseSnapshot(id, persister, snapshot.getId());
        entityEntry.managed(id);
    }

    private void delete(EntityPersister<?> persister, Integer id, Snapshot snapshot) {
        persister.delete(snapshot.getId());
        entityEntry.gone(id);
    }

    private void update(EntityPersister<?> persister, Integer id, Snapshot snapshot) {
        persister.update(snapshot, snapshot.getId());
        persistenceContext.getDatabaseSnapshot(id, persister, snapshot.getId());
        entityEntry.managed(id);
    }

    private <T> EntityPersister<?> getPersister(Class<T> tClass) {
        return persisterMap.get(tClass.getName());
    }
}
