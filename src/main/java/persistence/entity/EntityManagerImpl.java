package persistence.entity;

import jdbc.JdbcTemplate;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContextImpl(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> entityClass, Long id) {
        return (T) persistenceContext.getEntity(entityClass, id);
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
