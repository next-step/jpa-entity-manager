package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.sql.dml.DmlQueryBuilder;

import java.util.HashMap;
import java.util.Map;

public class MyEntityPersister implements EntityPersister {

    private final Map<Class<?> , RowMapper<?>> rowMappers;
    private final JdbcTemplate jdbcTemplate;

    public MyEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        final HashMap<Class<?>, RowMapper<?>> rowMappers = new HashMap<>();
        rowMappers.put(Person.class, new PersonRowMapper());
        this.rowMappers = rowMappers;
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        final DmlQueryBuilder<?> queryBuilder = new DmlQueryBuilder<>(entity.getClass());
        final String sql = queryBuilder.findById(id);
        return jdbcTemplate.queryForObject(sql, rowMappers.get(entity.getClass()));
    }

    @Override
    public Object getCachedDatabaseSnapshot(Long id) {
        return null;
    }

    @Override
    public Object load(Class<?> clazz, Long id) {
        final DmlQueryBuilder<?> queryBuilder = new DmlQueryBuilder<>(clazz);
        final String sql = queryBuilder.findById(id);
        final Object instance = jdbcTemplate.queryForObject(sql, rowMappers.get(clazz));
        if (instance == null) {
            throw new IllegalArgumentException("ObjectNotFoundException");
        }
        return instance;
    }

    @Override
    public void insert(Object entity) {
        final Class<?> clazz = entity.getClass();
        final DmlQueryBuilder<?> dmlQueryBuilder = new DmlQueryBuilder<>(clazz);
        final String sql = dmlQueryBuilder.insert(entity);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void update(Object entity) {

    }

    @Override
    public void delete(Object entity) {
        final String deleteSql = new DmlQueryBuilder<>(entity.getClass()).delete(entity);
        jdbcTemplate.execute(deleteSql);
    }
}
