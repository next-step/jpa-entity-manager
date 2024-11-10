package persistence.entity.impl;

import java.sql.Connection;
import java.text.MessageFormat;
import jdbc.JdbcTemplate;
import persistence.entity.EntityEntry;
import persistence.entity.EntityId;
import persistence.entity.EntityLoader;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersister;
import persistence.entity.EntityStatus;
import persistence.entity.PersistenceContext;
import persistence.exception.NotExistException;

public class DefaultEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext context;
    private final EntityPersister persister;
    private final EntityLoader loader;

    public DefaultEntityManager(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.context = new DefaultPersistenceContext();
        this.persister = new DefaultEntityPersister(jdbcTemplate);
        this.loader = new DefaultEntityLoader(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        EntityEntry entry = context.getEntity(id, clazz);
        if (entry == null) {
            return loadEntity(clazz, id);
        }

        Object entity = entry.entity();
        if (entity == null) {
            throw new NotExistException(MessageFormat.format("Entity id: {0}, type: {1}", id, clazz.getSimpleName()));
        }
        return clazz.cast(entity);
    }

    private <T> T loadEntity(Class<T> clazz, Object id) {
        EntityEntry entry = context.addLoadingEntity(id, clazz);

        T loadEntity = loader.load(clazz, id);
        entry.updateEntity(loadEntity);
        entry.updateStatus(EntityStatus.MANAGED);
        entry.createSnapshot(loadEntity);

        return loadEntity;
    }

    @Override
    public void persist(Object entity) {
        persister.insert(entity);
        context.addEntity(entity, EntityStatus.SAVING);
    }

    @Override
    public void remove(Object entity) {
        persister.delete(entity);
        context.removeEntity(entity);
    }

    @Override
    public <T> T merge(T entity) {
        Class<?> entityType = entity.getClass();
        EntityId entityId = new EntityId(entity, entityType);
        EntityEntry entry = context.getEntity(entityId.id(), entityType);

        if (entry.isDirty(entity)) {
            entry.updateEntity(entity);
            persister.update(entity);
            entry.updateSnapshot(entity);
        }

        return entity;
    }

}
