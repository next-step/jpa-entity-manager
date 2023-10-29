package persistence.entity;

import jdbc.JdbcTemplate;

public class DefaultEntityManager implements EntityManager {
    private final DefaultPersistenceContext persistenceContext;

    private DefaultEntityManager(DefaultPersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public static DefaultEntityManager of(JdbcTemplate jdbcTemplate) {
        return new DefaultEntityManager(DefaultPersistenceContext.of(jdbcTemplate));
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return persistenceContext.getEntity(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }
}
