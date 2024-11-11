package persistence.entity.impl;

import static persistence.entity.EntityStatus.DELETED;
import static persistence.entity.EntityStatus.LOADING;
import static persistence.entity.EntityStatus.MANAGED;

import java.sql.Connection;
import java.util.Optional;
import jdbc.JdbcTemplate;
import persistence.entity.EntityEntry;
import persistence.entity.EntityId;
import persistence.entity.EntityLoader;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersister;
import persistence.entity.EntitySnapshot;
import persistence.entity.PersistenceContext;

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
        Optional<T> entity = context.getEntity(id, clazz);
        return entity.orElseGet(() -> loadEntity(clazz, id));

    }

    private <T> T loadEntity(Class<T> clazz, Object id) {
        T loadEntity = loader.load(clazz, id);

        EntityEntry entry = context.addEntityEntry(loadEntity, LOADING);
        context.addEntity(loadEntity);
        context.addDatabaseSnapshot(loadEntity);

        entry.updateStatus(MANAGED);

        return loadEntity;
    }

    @Override
    public void persist(Object entity) {
        context.addEntity(entity);

        Object saveEntity = persister.insert(entity);
        context.addEntityEntry(saveEntity, MANAGED);
    }

    @Override
    public void remove(Object entity) {
        context.updateEntityEntry(entity, DELETED);
        persister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        Class<?> entityType = entity.getClass();
        EntityId entityId = new EntityId(entity, entityType);
        EntitySnapshot snapshot = context.getDatabaseSnapshot(entityId.id(), entityType);

        if (snapshot.hasDifferenceWith(entity)) {
            persister.update(entity);
            context.removeEntity(entity);
            context.addEntity(entity);
        }

        return entity;
    }

}
