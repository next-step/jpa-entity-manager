package hibernate.entity;

import hibernate.dml.SelectQueryBuilder;
import jdbc.JdbcTemplate;
import jdbc.ReflectionRowMapper;

public class EntityManagerImpl implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final EntityPersisters entityPersisters;
    private final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

    public EntityManagerImpl(final JdbcTemplate jdbcTemplate, EntityPersisters entityPersisters) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersisters = entityPersisters;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        final String query = selectQueryBuilder.generateQuery(new EntityClass<>(clazz), id);
        return jdbcTemplate.queryForObject(query, new ReflectionRowMapper<>(clazz));
    }

    @Override
    public void persist(final Object entity) {
        entityPersisters.findEntityPersister(entity)
                .insert(entity);
    }

    @Override
    public void remove(final Object entity) {
        entityPersisters.findEntityPersister(entity)
                .delete(entity);
    }
}
