package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.common.DtoMapper;
import persistence.sql.dml.SelectQueryBuilder;

public class EntityManagerImpl<T> implements EntityManager<T>{

    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public T find(Class<T> clazz, Long id) {
        String query = new SelectQueryBuilder(clazz).getFindById(id);
        return jdbcTemplate.queryForObject(query, new DtoMapper<>(clazz));
    }

    @Override
    public Object persist(Object entity) {
        return entityPersister.insert(entity);
    }

    @Override
    public boolean update(Object entity) {
        return entityPersister.update(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
