package persistence.entity;

import jdbc.JdbcTemplate;

public class MyEntityManager implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;

    public MyEntityManager(JdbcTemplate jdbcTemplate) {
        this.persistenceContext = new PersistenceContext();
        this.entityPersister = new MyEntityPersister(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return persistenceContext.get(clazz, id)
                .map(entity -> (T) entity)
                .orElse((T) entityPersister.load(clazz, id));
    }

    @Override
    public void persist(Object entity) {
        if (persistenceContext.contains(entity)) {
            return;
        }
        persistenceContext.persist(entity);
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        if (!persistenceContext.contains(entity)) {
            return;
        }
        persistenceContext.remove(entity);
        entityPersister.delete(entity);
    }


}
