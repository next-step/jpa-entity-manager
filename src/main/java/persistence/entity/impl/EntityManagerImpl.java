package persistence.entity.impl;

import java.sql.Connection;
import jdbc.TransactionalJdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final TransactionalJdbcTemplate transactionalJdbcTemplate;

    public EntityManagerImpl(Connection connection) {
        this.transactionalJdbcTemplate = new TransactionalJdbcTemplate(connection);
        this.persistenceContext = new PersistenceContextImpl(transactionalJdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return persistenceContext.find(entityClass, primaryKey);
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.persist(entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.remove(entity);
    }

    @Override
    public void update(Object entity) throws IllegalAccessException {
        persistenceContext.update(entity);
    }

    @Override
    public void flush() throws IllegalAccessException {
        for (Object entity : persistenceContext.getPendingEntities()) {
            new EntityPersister<>(entity.getClass(), transactionalJdbcTemplate).insert(entity);
        }
        for (Object entity : persistenceContext.getPersistedEntities()) {
            new EntityPersister<>(entity.getClass(), transactionalJdbcTemplate).update(entity);
        }
    }

    @Override
    public TransactionalJdbcTemplate getTransaction() {
        return this.transactionalJdbcTemplate;
    }

}
