package persistence.entity;

import java.sql.Connection;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;

public class DefaultEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final PersistenceContext context;
    private final EntityPersister persister;
    private final EntityLoader loader;

    public DefaultEntityManager(Connection connection, Dialect dialect) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.dialect = dialect;
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

    @Override
    public Dialect getDialect() {
        return this.dialect;
    }

}
