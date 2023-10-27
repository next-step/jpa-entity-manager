package persistence.entity;

import java.util.List;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {

    private Map<String, EntityPersister<?>> persisterMap;
    private Map<String, EntityLoader<?>> loaderMap;

    public EntityManagerImpl(Map<String, EntityPersister<?>> persisterMap, Map<String, EntityLoader<?>> loaderMap) {
        this.persisterMap = persisterMap;
        this.loaderMap = loaderMap;
    }

    @Override
    public <T> List<T> findAll(Class<T> tClazz) {
        return getLoader(tClazz).findAll();
    }

    @Override
    public <R, I> R find(Class<R> rClass, I i) {
        return getLoader(rClass).findById(i);
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

    private <T> EntityLoader<T> getLoader(Class<T> tClass) {
        return (EntityLoader<T>) loaderMap.get(tClass.getName());
    }
}
