package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.conditions.WhereRecord;

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

    @Override
    public Object persist(Object entity) {
        entityPersister.insert(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
