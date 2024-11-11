package persistence.entity.impl;

import java.sql.Connection;
import java.util.List;
import jdbc.TransactionalJdbcTemplate;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final TransactionalJdbcTemplate transactionalJdbcTemplate;
    private final EntityPersister entityPersister;

    public EntityManagerImpl(Connection connection) {
        this.transactionalJdbcTemplate = new TransactionalJdbcTemplate(connection);
        this.persistenceContext = new PersistenceContextImpl();
        this.entityPersister = new EntityPersister(transactionalJdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        T entity = persistenceContext.getEntity(entityClass, primaryKey);
        if (entity == null) {
            entity = entityClass.cast(
                entityPersister.findById(entityClass, primaryKey)
            );
            persistenceContext.addEntity(entity);
        }

        return entity;
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }

    @Override
    public void flush() throws IllegalAccessException {
        for (Object entity : persistenceContext.getSavingEntities()) {
            Object entityWithId = entityPersister.insert(entity);
            persistenceContext.mangeEntity(entityWithId);
        }

        for (Object entity : persistenceContext.getDeletedEntities()) {
            entityPersister.delete(entity);
            persistenceContext.detachEntity(entity);
        }

        for (Object entity : persistenceContext.getManagedEntities()) {
            List<String> changedColumns = persistenceContext.findDirtyColumns(entity);
            if (!changedColumns.isEmpty()) {
                entityPersister.update(entity, changedColumns);
                persistenceContext.updateDatabaseSnapshot(entity);
            }
        }
    }

    @Override
    public TransactionalJdbcTemplate getTransaction() {
        transactionalJdbcTemplate.setEntityManager(this);
        return this.transactionalJdbcTemplate;
    }

}
