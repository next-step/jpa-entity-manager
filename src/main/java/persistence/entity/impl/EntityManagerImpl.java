package persistence.entity.impl;

import java.sql.Connection;
import java.util.List;
import jdbc.TransactionalJdbcTemplate;
import persistence.entity.DirtyCheck;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final TransactionalJdbcTemplate transactionalJdbcTemplate;
    private final EntityPersister entityPersister;
    private final DirtyCheck dirtyCheck;

    public EntityManagerImpl(Connection connection) {
        this.transactionalJdbcTemplate = new TransactionalJdbcTemplate(connection);
        this.persistenceContext = new PersistenceContextImpl();
        this.entityPersister = new EntityPersister(transactionalJdbcTemplate);
        this.dirtyCheck = new DirtyCheck(persistenceContext);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        T entity = persistenceContext.getEntity(entityClass, primaryKey);
        if (entity == null) {
            entity = entityClass.cast(
                entityPersister.findById(entityClass, primaryKey)
            );
            persistenceContext.captureDatabaseSnapshot(entity);
            persistenceContext.attachEntity(entity);
        }

        return entity;
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.attachEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.detachEntity(entity);
    }

    @Override
    public void flush() throws IllegalAccessException {
        for (Object entity : persistenceContext.getPendingEntities()) {
            entityPersister.insert(entity);
        }
        for (Object entity : persistenceContext.getPersistedEntities()) {
            List<String> changedColumns = dirtyCheck.findDirtyColumns(entity);
            if (!changedColumns.isEmpty()) {
                entityPersister.update(entity, changedColumns);
            }
        }
        persistenceContext.reset();
    }

    @Override
    public TransactionalJdbcTemplate getTransaction() {
        transactionalJdbcTemplate.setEntityManager(this);
        return this.transactionalJdbcTemplate;
    }

}
