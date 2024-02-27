package persistence.entity;

import jdbc.JdbcTemplate;

public class MyEntityManager implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public MyEntityManager(JdbcTemplate jdbcTemplate) {
        this.entityPersister = new MyEntityPersister(jdbcTemplate);
        this.entityLoader = new MyEntityLoader(jdbcTemplate);
        this.persistenceContext = new MyPersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            return entityLoader.find(clazz, id);
        }
        return entity;
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.addEntity(entity);
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }
}
