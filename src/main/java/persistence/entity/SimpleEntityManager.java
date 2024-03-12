package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.conditions.WhereRecord;
import persistence.sql.metadata.EntityMetadata;
import persistence.sql.metadata.PrimaryKeyMetadata;

import java.util.List;

public class SimpleEntityManager implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final Dialect dialect;
    private final EntityPersister entityPersister;

    public SimpleEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.entityPersister = new EntityPersister(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.builder()
                .dialect(dialect)
                .entity(clazz)
                .where(List.of(WhereRecord.of("id", "=", Id)))
                .build();

        return jdbcTemplate.queryForObject(selectQueryBuilder.generateQuery(), resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
    }

    private <T> T findByEntity(T entity) {
        Class<T> clazz = (Class<T>) entity.getClass();
        EntityMetadata metadata = EntityMetadata.of(clazz, entity);
        PrimaryKeyMetadata primaryKey = metadata.getPrimaryKey();

        SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.builder()
                .dialect(dialect)
                .entity(clazz)
                .where(List.of(WhereRecord.of(primaryKey.getName(), "=", primaryKey.getValue())))
                .build();

        return jdbcTemplate.queryForObject(selectQueryBuilder.generateQuery(), resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
    }

    @Override
    public Object persist(Object entity) {
        try {
            Object findEntity = findByEntity(entity);
            if (!entity.equals(findEntity)) {
                entityPersister.update(entity);
            }
        } catch (RuntimeException e) {
            entityPersister.insert(entity);
        }

        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
