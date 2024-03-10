package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.EntityPersister;
import persistence.sql.dml.builder.SelectQueryBuilder;
import persistence.sql.dml.model.DMLColumn;
import persistence.sql.model.Table;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final JdbcTemplate jdbcTemplate;

    public EntityManagerImpl(EntityPersister entityPersister, JdbcTemplate jdbcTemplate) {
        this.entityPersister = entityPersister;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        final String query = findQuery(clazz, id);

        return jdbcTemplate.queryForObject(query, new RowMapperImpl<>(clazz));
    }

    private <T> String findQuery(Class<T> clazz, Long id) {
        final SelectQueryBuilder queryBuilder = new SelectQueryBuilder(
                new Table(clazz), new DMLColumn(clazz)
        );

        return queryBuilder.findById(id).build();
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

}
