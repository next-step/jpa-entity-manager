package persistence.entity.impl;

import java.sql.Connection;
import jdbc.TransactionalJdbcTemplate;
import persistence.entity.DirtyCheck;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersisterStore;
import persistence.entity.PersistenceContext;

public class EntityManagerImpl implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final TransactionalJdbcTemplate transactionalJdbcTemplate;
    private final EntityPersisterStore entityPersisterStore;
    private final DirtyCheck dirtyCheck;

    public EntityManagerImpl(Connection connection) {
        this.transactionalJdbcTemplate = new TransactionalJdbcTemplate(connection);
        this.persistenceContext = new PersistenceContextImpl(transactionalJdbcTemplate);
        this.entityPersisterStore = new EntityPersisterStore(transactionalJdbcTemplate);
        this.dirtyCheck = new DirtyCheck(persistenceContext);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return persistenceContext.getEntity(entityClass, primaryKey);
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
        for (Object entity : persistenceContext.getPendingEntities()) {
            entityPersisterStore
                .getEntityPersister(entity.getClass())
                .insert(entity);
        }
        for (Object entity : persistenceContext.getPersistedEntities()) {
            if (dirtyCheck.isDirty(entity)) {
                entityPersisterStore
                    .getEntityPersister(entity.getClass())
                    .update(entity);
            }
        }
    }

    @Override
    public TransactionalJdbcTemplate getTransaction() {
        transactionalJdbcTemplate.setEntityManager(this);
        return this.transactionalJdbcTemplate;
    }

}
