package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import persistence.sql.common.instance.Value;
import persistence.sql.common.meta.Column;
import persistence.sql.common.meta.EntityMeta;
import persistence.sql.common.meta.TableName;
import persistence.sql.dml.QueryDml;
import persistence.sql.dml.SelectQuery;

import java.util.List;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    private TableName tableName;
    private Column[] columns;
    private Value[] values;

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

    private <T> void mapping(T t) {
        this.tableName = TableName.of(t.getClass());
        this.columns = Column.of(t.getClass().getDeclaredFields());
        this.values = Value.of(t.getClass());
    }
}
