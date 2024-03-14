package persistence.core;

import database.DatabaseServer;
import java.sql.SQLException;
import jdbc.JdbcTemplate;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(DatabaseServer server) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        this.persistenceContext = new DefaultPersistenceContext();
        this.entityLoader = new DefaultEntityLoader(jdbcTemplate);
        this.entityPersister = new DefaultEntityPersister(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityKey entityKey = persistenceContext.getEntityKey(clazz, id);
        T entity = (T) persistenceContext.getEntity(entityKey);
        if (entity == null) {
            EntityEntry entityEntry = new EntityEntry(Status.LOADING);
            persistenceContext.addEntityEntry(entityKey, entityEntry);
            entity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(entityKey, entity);
            persistenceContext.getDatabaseSnapshot(entityKey);
            entityEntry.updateStatus(Status.MANAGED);
        }
        return entity;
    }

    @Override
    public void persist(Object entity) {
        Long id = entityPersister.insert(entity);
        entityPersister.setIdentifier(entity, id);
        EntityEntry entityEntry = new EntityEntry(Status.SAVING);
        EntityKey entityKey = persistenceContext.getEntityKey(entity.getClass(), id);
        persistenceContext.addEntityEntry(entityKey, entityEntry);
        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.getDatabaseSnapshot(entityKey);
        entityEntry.updateStatus(Status.MANAGED);
    }

    @Override
    public void remove(Object entity) {
        Long id = entityPersister.getIdentifier(entity);
        EntityKey entityKey = persistenceContext.getEntityKey(entity.getClass(), id);
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entityKey);
        entityEntry.updateStatus(Status.DELETED);
        persistenceContext.removeEntity(entityKey);
        entityPersister.delete(entity);
        entityEntry.updateStatus(Status.GONE);
    }

    @Override
    public <T> T merge(T entity) {
        if (entityPersister.update(entity)) {
            Long id = entityPersister.getIdentifier(entity);
            EntityKey entityKey = persistenceContext.getEntityKey(entity.getClass(), id);
            persistenceContext.getDatabaseSnapshot(entityKey);
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
