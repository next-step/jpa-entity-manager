package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.dml.QueryDml;
import persistence.sql.dml.SelectQuery;

import java.util.List;

public class EntityPersister<T> {
    private final JdbcTemplate jdbcTemplate;
    private final ResultMapper<T> resultMapper;
    private final TableName tableName;
    private final Columns columns;

    public EntityPersister(JdbcTemplate jdbcTemplate, Class<T> tClass) {
        this.jdbcTemplate = jdbcTemplate;

        this.resultMapper = new ResultMapper<>(tClass);
        this.tableName = TableName.of(tClass);
        this.columns = Columns.of(tClass.getDeclaredFields());
    }

    public List<T> findAll() {
        String query = SelectQuery.create(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns);

        return jdbcTemplate.query(query, resultMapper);
    }

    public <I> T findById(I i) {
        String query = SelectQuery.create(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns, i);

        return jdbcTemplate.queryForObject(query, resultMapper);
    }

    public <T> boolean update(T t, Object arg) {
        try {
            jdbcTemplate.execute(QueryDml.update(getValues(t), tableName, columns, arg));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <I> void insert(I i) {
        jdbcTemplate.execute(QueryDml.insert(tableName, columns, getValues(i)));
    }

    public void delete(Object arg) {
        jdbcTemplate.execute(QueryDml.delete(tableName, columns, arg));
    }

    public <I> Values getValues(I i) {
        return Values.of(i);
    }
}