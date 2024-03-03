package persistence.core;

import database.DatabaseServer;
import jdbc.JdbcTemplate;

import java.sql.SQLException;

public class EntityManagerImpl implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;


    public EntityManagerImpl(DatabaseServer server) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(server.getConnection());
        this.entityPersister = new DefaultEntityPersister(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) throws Exception {

        return entityPersister.select(clazz, Id);
    }

    @Override
    public <T> T persist(T entity) throws SQLException {
        entityPersister.insert(entity);

        return entity;
    }

    @Override
    public void remove(Object entity) throws Exception {
        entityPersister.delete(entity);
    }

    @Override
    public void update(Object entity) throws Exception {
        entityPersister.update(entity);
    }
}
