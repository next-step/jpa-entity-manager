package persistence.core;

import database.DatabaseServer;
import jdbc.JdbcTemplate;

import java.sql.SQLException;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(DatabaseServer server) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
        this.entityLoader = new DefaultEntityLoader(jdbcTemplate);
        this.entityPersister = new DefaultEntityPersister(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Object Id) {
        return entityLoader.find(clazz, (Long) Id);
    }

    @Override
    public void persist(Object entity) {
        Long id = entityPersister.insert(entity);
        entityPersister.setIdentifier(entity, id);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

}
