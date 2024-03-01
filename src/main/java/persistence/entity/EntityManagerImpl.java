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
        Long pkValue = entityPersist.getPKValue(entity);
        if (pkValue == null) {
            entityPersist.insert(entity);
            return;
        }
        T findEntity = entityPersist.findOne(entity, pkValue);
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
