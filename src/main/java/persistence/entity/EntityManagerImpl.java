package persistence.entity;

import java.util.List;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {

    private final Map<String, EntityPersister<?>> persisterMap;

    EntityManagerImpl(Map<String, EntityPersister<?>> persisterMap) {
        this.persisterMap = persisterMap;
    }

    @Override
    public <T> List<T> findAll(Class<T> tClazz) {
        return getPersister(tClazz).findAll();
    }

    @Override
    public <R, I> R find(Class<R> rClass, I input) {
        return getPersister(rClass).findById(input);
    }

    @Override
    public <T> T persist(T t) {
        getPersister(t.getClass()).insert(t);
        return t;
    }

    @Override
    public <T> void remove(Class<T> tClass, Object arg) {
        getPersister(tClass).delete(arg);
    }

    @Override
    public <T> void update(T t, Object arg) {
        getPersister(t.getClass()).update(t, arg);
    }

    private <T> EntityPersister<T> getPersister(Class<T> tClass) {
        return (EntityPersister<T>) persisterMap.get(tClass.getName());
    }
}
