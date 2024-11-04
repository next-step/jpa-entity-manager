package persistence.entity;

import java.sql.Connection;
import jdbc.JdbcTemplate;

public class DefaultEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext context;
    private final EntityPersister persister;
    private final EntityLoader loader;

    public DefaultEntityManager(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.context = new DefaultPersistenceContext();
        this.persister = new DefaultEntityPersister(jdbcTemplate);
        this.loader = new DefaultEntityLoader(jdbcTemplate, context);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return loader.load(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        context.addEntity(entity);
        persister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        context.removeEntity(entity);
        persister.delete(entity);
    }

}
