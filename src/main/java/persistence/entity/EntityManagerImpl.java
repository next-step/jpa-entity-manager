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
        return getPersister(tClazz).findAll();
    }

    @Override
    public <R, I> R find(Class<R> rClass, I input) {
        EntityPersister<R> persister = getPersister(rClass);
        int id = persister.getHashCode(input);

        if(!persistenceContext.isValidEntity(id)) {
            persistenceContext.addEntity(id, persister.findById(input));
        }

        return (R) persistenceContext.getEntity(id);
    }

    @Override
    public <T> T persist(T t) {
        final EntityPersister<T> persister = (EntityPersister<T>) getPersister(t.getClass());
        final String value =  persister.getIdValue(t);
        int contextId = persister.getHashCode(value);

        if(persistenceContext.isValidEntity(contextId)) {
            throw new IllegalArgumentException();
        }

        getPersister(t.getClass()).insert(t);
        persistenceContext.addEntity(contextId, persister.findById(value));

        return (T) persistenceContext.getEntity(contextId);
    }

    @Override
    public <T> void remove(Class<T> tClass, Object arg) {
        final EntityPersister<T> persister = getPersister(tClass);
        int id = persister.getHashCode(arg);

        if(!persistenceContext.isValidEntity(id)) {
            throw new RuntimeException();
        }

        getPersister(tClass).delete(arg);
        persistenceContext.removeEntity(id);
    }

    @Override
    public <T> void update(T t, Object arg) {
        final EntityPersister<T> persister = (EntityPersister<T>) getPersister(t.getClass());
        int id = persister.getHashCode(arg);

        if(persistenceContext.getEntity(id) != null) {
            getPersister(t.getClass()).update(t, arg);
            persistenceContext.removeEntity(id);
            persistenceContext.addEntity(id, persister.findById(arg));
        }
    }

    private <T> EntityPersister<T> getPersister(Class<T> tClass) {
        return (EntityPersister<T>) persisterMap.get(tClass.getName());
    }
}
