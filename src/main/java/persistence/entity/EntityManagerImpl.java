package persistence.entity;

import jdbc.JdbcTemplate;

public class EntityManagerImpl<T> implements EntityManager<T> {
    private final EntityPersist entityPersist;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityPersist = new EntityPersist(jdbcTemplate);
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        return entityPersist.findOneOrFail(clazz, id);
    }

    @Override
    public void persist(T entity) {
        entityPersist.persist(entity);
    }

    @Override
    public void remove(T entity) {
        entityPersist.delete(entity);
    }
}
