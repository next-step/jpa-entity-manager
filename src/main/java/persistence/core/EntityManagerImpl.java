package persistence.core;

import database.DatabaseServer;
import jdbc.JdbcTemplate;

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    private static final Logger LOG = LoggerFactory.getLogger(EntityManagerImpl.class);
    public EntityManagerImpl(DatabaseServer server) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        this.persistenceContext = new DefaultPersistenceContext();
        this.entityLoader = new DefaultEntityLoader(jdbcTemplate);
        this.entityPersister = new DefaultEntityPersister(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = (T) persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            EntityEntry entityEntry = new EntityEntry(Status.LOADING);
            persistenceContext.addEntityEntry(clazz, id, entityEntry);
            entity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(id, entity);
            persistenceContext.getDatabaseSnapshot(id, entity);
            entityEntry.updateStatus(Status.MANAGED);
        }
        return entity;
    }

    @Override
    public void persist(Object entity) {
        Long id = entityPersister.insert(entity);
        EntityEntry entityEntry = new EntityEntry(Status.SAVING);
        persistenceContext.addEntity(id, entity);
        persistenceContext.addEntityEntry(entity.getClass(), id, entityEntry);
        entityPersister.setIdentifier(entity, id);
        persistenceContext.getDatabaseSnapshot(id, entity);
        entityEntry.updateStatus(Status.MANAGED);
    }

    @Override
    public void remove(Object entity) {
        Long id = entityPersister.getIdentifier(entity);
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity.getClass(), id);
        persistenceContext.removeEntity(id, entity.getClass());
        entityPersister.delete(entity);
        entityEntry.updateStatus(Status.GONE);
    }

    @Override
    public <T> T merge(T entity) {
        if (entityPersister.update(entity)) {
            persistenceContext.getDatabaseSnapshot(1L, entity);
            return entity;
        }

        return null;
    }

    @Override
    public void flush() {
        persistenceContext.dirtyCheck()
                .forEach(this::merge);
    }

}
