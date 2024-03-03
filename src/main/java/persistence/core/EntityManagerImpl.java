package persistence.core;

import database.DatabaseServer;
import jdbc.JdbcTemplate;
import persistence.entity.EntityValue;

import java.sql.SQLException;
import java.util.List;

public class EntityManagerImpl implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final EntityValue entityValue;


    public EntityManagerImpl(DatabaseServer server, EntityLoader entityLoader, EntityValue entityValue) throws SQLException {
        this.jdbcTemplate = new JdbcTemplate(server.getConnection());
        this.entityLoader = entityLoader;
        this.entityValue = entityValue;
        this.entityPersister = new DefaultEntityPersister(jdbcTemplate);
    }

    @Override
    public <T> List<T> find(Class<T> clazz) throws Exception {
        return entityLoader.find(clazz);
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) throws Exception {
        return entityLoader.find(clazz, Id);
    }

    @Override
    public <T> T persist(T entity) throws Exception {
        Long id = entityPersister.insert(entity);

        return (T) entityLoader.find(entity.getClass(), id);
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
