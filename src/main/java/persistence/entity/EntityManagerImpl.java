package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.meta.pk.PKField;

import java.lang.reflect.Field;
import java.util.Optional;

public class EntityManagerImpl<T> implements EntityManager<T> {
    private final EntityLoader entityLoader;
    private final EntityPersist entityPersist;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersist = new EntityPersist(jdbcTemplate);
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        return entityLoader.findOne(clazz, id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("entity id: %s not found", id)));
    }

    @Override
    public void persist(T entity) {
        Long pkValue = getPKValue(entity);
        if (pkValue == null) {
            entityPersist.insert(entity);
            return;
        }
        Optional<T> findEntity = entityLoader.findOne(entity, pkValue);
        if (findEntity.isEmpty()) {
            entityPersist.insert(entity);
            return;
        }
        entityPersist.update(pkValue, entity);
    }

    @Override
    public void remove(T entity) {
        entityPersist.delete(entity);
    }

    private Long getPKValue(T entity) {
        Field pkField = new PKField(entity.getClass()).getField();
        pkField.setAccessible(true);
        Long value;
        try {
            value = (Long) pkField.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        return value;
    }
}
