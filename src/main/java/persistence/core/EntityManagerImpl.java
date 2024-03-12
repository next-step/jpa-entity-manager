package persistence.core;

import database.DatabaseServer;
import jdbc.JdbcTemplate;

import java.sql.SQLException;

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
        T entity = (T) persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            entity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(id, entity);
            persistenceContext.getDatabaseSnapshot(id, entity);
        }
        return entity;
    }

    @Override
    public void persist(Object entity) {
        Long id = entityPersister.insert(entity);
        entityPersister.setIdentifier(entity, id);
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }

    @Override
    public void remove(Object entity) {
        Long id = entityPersister.getIdentifier(entity);
        persistenceContext.removeEntity(id, entity);
        entityPersister.delete(entity);
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
