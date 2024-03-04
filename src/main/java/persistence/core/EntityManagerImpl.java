package persistence.core;

import database.DatabaseServer;
import jdbc.JdbcTemplate;

import java.sql.SQLException;

public class EntityManagerImpl implements EntityManager {
    private final JdbcTemplate jdbcTemplate;

    public EntityManagerImpl(DatabaseServer server) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(server.getConnection());
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) throws Exception {
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, clazz);

        return entityPersister.select(clazz, Id);
    }

    @Override
    public <T> T persist(T entity) throws SQLException {
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entity.getClass());

        entityPersister.insert(entity);

        return entity;
    }

    @Override
    public void remove(Object entity) throws Exception {
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entity.getClass());

        entityPersister.delete(entity);
    }

    @Override
    public void update(Object entity) throws Exception {
        EntityPersister entityPersister = new EntityPersister(jdbcTemplate, entity.getClass());

        entityPersister.update(entity);
    }
}
