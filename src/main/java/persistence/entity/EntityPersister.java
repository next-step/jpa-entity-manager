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

    public <I> T findById(I input) {
        String query = SelectQuery.create(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns, input);

        return jdbcTemplate.queryForObject(query, resultMapper);
    }

    public <I> boolean update(I input, Object arg) {
        try {
            jdbcTemplate.execute(QueryDml.update(getValues(input), tableName, columns, arg));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <I> void insert(I input) {
        jdbcTemplate.execute(QueryDml.insert(tableName, columns, getValues(input)));
    }

    public void delete(Object arg) {
        jdbcTemplate.execute(QueryDml.delete(tableName, columns, arg));
    }

    public <I> Values getValues(I input) {
        return Values.of(input);
    }
}
