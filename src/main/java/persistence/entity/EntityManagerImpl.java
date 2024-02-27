package persistence.entity;

import persistence.sql.dml.DmlQueryBuilder;

import java.sql.Connection;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    public EntityManagerImpl(Connection connection, DmlQueryBuilder dmlQueryBuilder) {
        this.entityPersister = new EntityPersister(connection, dmlQueryBuilder);
        this.entityLoader = new EntityLoader(connection, dmlQueryBuilder);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return entityLoader.find(clazz, id);
    }

    @Override
    public <T> void persist(T entity) {
        entityPersister.insert(entity);
    }

    @Override
    public <T> boolean update(T entity, Object id) {
        return entityPersister.update(entity, id);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
