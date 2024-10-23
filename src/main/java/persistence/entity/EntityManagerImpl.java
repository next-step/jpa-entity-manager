package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.model.EntityPrimaryKey;
import persistence.sql.dml.DmlQueryBuilder;

public class EntityManagerImpl implements EntityManager {
    private final DmlQueryBuilder queryBuilder;

    private final JdbcTemplate jdbcTemplate;

    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(
            DmlQueryBuilder queryBuilder,
            JdbcTemplate jdbcTemplate,
            PersistenceContext persistenceContext
    ) {
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T findById(Class<T> clazz, Long id) {
        T entity = persistenceContext.getEntity(clazz, id);
        if (entity != null) {
            return entity;
        }

        String selectQuery = queryBuilder.buildSelectByIdQuery(clazz, id);
        return jdbcTemplate.queryForObject(selectQuery, resultSet ->
                new RowMapperImpl<>(clazz).mapRow(resultSet)
        );
    }

    @Override
    public void persist(Object entity) {
        jdbcTemplate.execute(queryBuilder.buildInsertQuery(entity));

        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        jdbcTemplate.execute(queryBuilder.buildDeleteQuery(entity));

        persistenceContext.removeEntity(entity);
    }

    @Override
    public void merge(Object entity) {
        Class<?> entityClass = entity.getClass();
        Object entityId = EntityPrimaryKey.build(entity).keyValue();
        Object existingEntity = persistenceContext.getEntity(entityClass, entityId);

        if (existingEntity != null) {
            jdbcTemplate.execute(queryBuilder.buildUpdateQuery(entity));
        } else {
            persist(entity);
        }
    }
}
