package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import persistence.sql.common.instance.Value;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.dml.QueryDml;
import persistence.sql.dml.SelectQuery;

import java.util.List;

public class EntityPersister<T> {
    private final JdbcTemplate jdbcTemplate;
    private Class<T> tClass;

    private TableName tableName;
    private Columns columns;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public EntityPersister(JdbcTemplate jdbcTemplate, Class<T> tClass) {
        this.jdbcTemplate = jdbcTemplate;

        this.tClass = tClass;
        this.tableName = TableName.of(tClass);
        this.columns = Columns.of(tClass.getDeclaredFields());
    }

    public List<T> findAll() {
        String query = SelectQuery.create(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns);

        return jdbcTemplate.query(query, new ResultMapper<>(tClass));
    }

    public <I> T findById(I i) {
        String query = SelectQuery.create(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns, i);

        return jdbcTemplate.queryForObject(query, new ResultMapper<>(tClass));
    }

    public <T> boolean update(T t, Object arg) {
        try {
            jdbcTemplate.execute(QueryDml.update(t, tableName, columns, arg));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <T> void insert(T t) {
        final TableName tableName = TableName.of(t.getClass());
        final Columns columns = Columns.of(t.getClass().getDeclaredFields());

        jdbcTemplate.execute(QueryDml.insert(tableName, columns, t));
    }

    public void delete(Object arg) {
        jdbcTemplate.execute(QueryDml.delete(tableName, columns, arg));
    }
}
