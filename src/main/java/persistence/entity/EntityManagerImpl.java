package persistence.entity;

import jdbc.JdbcTemplate;

public class EntityManagerImpl<T> implements EntityManager<T> {
    private final EntityLoader entityLoader;
    private final EntityPersist entityPersist;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersist = new EntityPersist(jdbcTemplate);
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        return entityLoader.findOneOrFail(clazz, id);
    }

    @Override
    public void persist(T entity) {
        Long pkValue = entityPersist.getPKValue(entity);
        if (pkValue == null) {
            entityPersist.insert(entity);
            return;
        }
        T findEntity = entityLoader.findOne(entity, pkValue);
        if (findEntity == null) {
            entityPersist.insert(entity);
            return;
        }
        entityPersist.update(pkValue, entity);
    }

    @Override
    public void remove(T entity) {
        entityPersist.delete(entity);
    }
}
