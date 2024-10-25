package persistence.entity.impl;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import jdbc.TransactionalJdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.PersistenceContext;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final JdbcTemplate transactionalJdbcTemplate;

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
        persistenceContext.flush();
    }

    @Override
    public TransactionalJdbcTemplate getTransaction() {
        return (TransactionalJdbcTemplate) this.transactionalJdbcTemplate;
    }

}
