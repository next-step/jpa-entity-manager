package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import persistence.sql.dml.QueryDml;
import persistence.sql.dml.SelectQuery;

import java.util.List;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> List<T> findAll(Class<T> tClass) {
        String query = SelectQuery.create(tClass, new Object() {
        }.getClass().getEnclosingMethod().getName());

        return jdbcTemplate.query(query, new ResultMapper<>(tClass));
    }

    public <T, R> T findById(Class<T> tClass, R r) {
        String query = SelectQuery.create(tClass, new Object() {
        }.getClass().getEnclosingMethod().getName(), r);

        return jdbcTemplate.queryForObject(query, new ResultMapper<>(tClass));
    }

    public <T> boolean update(T t, Object arg) {
        try {
            jdbcTemplate.execute(QueryDml.update(t, arg));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <T> void insert(T t) {
        jdbcTemplate.execute(QueryDml.insert(t));
    }

    public <T> void delete(T t, Object arg) {
        jdbcTemplate.execute(QueryDml.delete(t, arg));
    }
}
