package persistence.entity;

import jdbc.JdbcTemplate;

import java.sql.Connection;
import java.util.List;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;

    public EntityManagerImpl(Connection connection) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        return entityPersister.findAll(tClass);
    }

    @Override
    public <R, I> R find(Class<R> rClass, I i) {
        return (R) entityPersister.findById(rClass, i);
    }

    @Override
    public <T> T persist(T t) {
        entityPersister.insert(t);
        return t;
    }

    @Override
    public void remove(Object arg) {
        entityPersister.delete(arg);
    }

    @Override
    public <T> void update(T t, Object arg) {
        entityPersister.update(t, arg);
    }
}
