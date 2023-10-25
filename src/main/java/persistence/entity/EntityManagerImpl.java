package persistence.entity;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdbc.JdbcTemplate;

public class EntityManagerImpl implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final Map<String, EntityPersister<?>> persisterMap;
    private final Map<String, EntityLoader<?>> loaderMap;


    public EntityManagerImpl(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.persisterMap = new HashMap<>();
        this.loaderMap = new HashMap<>();
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
        String key = tClass.getName();

        if (persisterMap.get(key) == null) {
            persisterMap.put(key, new EntityPersister<>(jdbcTemplate, tClass));
        }

        return (EntityPersister<T>) persisterMap.get(key);
    }

    private <T> EntityLoader<T> getLoader(Class<T> tClass) {
        String key = tClass.getName();

        if (loaderMap.get(key) == null) {
            loaderMap.put(key, new EntityLoader<>(jdbcTemplate, tClass));
        }

        return (EntityLoader<T>) loaderMap.get(key);
    }
}
