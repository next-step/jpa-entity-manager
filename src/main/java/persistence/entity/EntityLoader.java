package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import persistence.sql.QueryUtil;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

import java.util.List;

public class EntityLoader<T> {
    private final JdbcTemplate jdbcTemplate;
    private final ResultMapper<T> resultMapper;
    private final TableName tableName;
    private final Columns columns;

    public EntityLoader(JdbcTemplate jdbcTemplate, Class<T> tClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.resultMapper = new ResultMapper<>(tClass);

        this.tableName = TableName.of(tClass);
        this.columns = Columns.of(tClass.getDeclaredFields());
    }

    public List<T> findAll() {
        String query = QueryUtil.select().get(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns);

        return jdbcTemplate.query(query, resultMapper);
    }

    public <I> T findById(I input) {
        String query = QueryUtil.select().get(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns, input);

        return jdbcTemplate.queryForObject(query, resultMapper);
    }
}
