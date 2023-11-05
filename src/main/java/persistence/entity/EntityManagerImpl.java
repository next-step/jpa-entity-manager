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

        return (R) persistenceContext.getEntity(key, persister, input);
    }

    @Override
    public <T> T persist(T t) {
        final EntityPersister<?> persister = getPersister(t.getClass());
        Object id = persister.getIdValue(t);
        int key = persister.getHashCode(id);

        return (T) persistenceContext.addEntity(key, id, persister.getEntity(t));
    }

    @Override
    public <T> void remove(T t, Object arg) {
        final EntityPersister<?> persister = getPersister(t.getClass());
        int key = persister.getHashCode(arg);

        persistenceContext.removeEntity(key);
    }

    public void flush() {
        persistenceContext.flush(persisterMap);
    }

    private <T> EntityPersister<?> getPersister(Class<T> tClass) {
        return persisterMap.get(tClass.getName());
    }
}
