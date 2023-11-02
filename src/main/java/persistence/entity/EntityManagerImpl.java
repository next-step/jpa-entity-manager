package persistence.entity;

import java.util.List;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final Map<String, EntityPersister<?>> persisterMap;

    EntityManagerImpl(Map<String, EntityPersister<?>> persisterMap) {
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

        persistenceContext.addEntity(key, persistenceContext.getDatabaseSnapshot(key, persister, input));

        return (R) persistenceContext.getEntity(key);
    }

    @Override
    public <T> T persist(T t) {
        final EntityPersister<?> persister = getPersister(t.getClass());
        Object id = persister.getIdValue(t);
        int key = persister.getHashCode(id);

        persistenceContext.addEntity(key, id, t);

        return (T) persistenceContext.getEntity(key);
    }

    @Override
    public <T> void remove(T t, Object arg) {
        final EntityPersister<?> persister = getPersister(t.getClass());
        int id = persister.getHashCode(arg);

        persistenceContext.removeEntity(id);
    }

    public void flush() {
        persistenceContext.comparison().forEach((id, data) -> {
            final EntityPersister<?> persister = getPersister(data.getObjectClass());

            if(!persistenceContext.isEntityInSnapshot(id)) {
                persister.insert(data.getObject());
                persistenceContext.getDatabaseSnapshot(id, persister, data.getId());
                return;
            }

            if(!persistenceContext.isEntityInContext(id)) {
                persister.delete(data.getId());
                return;
            }

            persister.update(data, data.getId());
            persistenceContext.getDatabaseSnapshot(id, persister, data.getId());
        });
    }

    private <T> EntityPersister<?> getPersister(Class<T> tClass) {
        return persisterMap.get(tClass.getName());
    }
}
