package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.Query;
import persistence.sql.entity.EntityData;
import util.ReflectionUtil;

public class SimpleEntityManager implements EntityManager {

    private final Query query;
    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;

    public SimpleEntityManager(Query query, JdbcTemplate jdbcTemplate, EntityPersister entityPersister) {
        this.query = query;
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = entityPersister;
    }

    @Override
    public <T, K> T find(Class<T> clazz, K id) {
        return jdbcTemplate.queryForObject(query.findById(clazz, id), new SimpleRowMapper<>(clazz));
    }

    @Override
    public void persist(Object entity) {
        Class<?> entityClass = entity.getClass();
        EntityData entityData = new EntityData(entityClass);

        Object idValue = ReflectionUtil.getValueFrom(entityData.getEntityColumns().getIdColumn().getField(), entity);
        if (idValue == null) {
            entityPersister.insert(entity);
            return;
        }

        Object foundEntity = find(entityClass, idValue);
        if (foundEntity == null) {
            entityPersister.insert(entity);
        } else {
            entityPersister.update(entity);
        }
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

}
