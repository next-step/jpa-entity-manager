package persistence.entity;

import java.util.List;
import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;
import persistence.sql.dml.Query;

public class EntityLoader<T> {

    private final Query query;
    private final JdbcTemplate jdbcTemplate;
    private final ResultMapper<T> resultMapper;
    private final TableName tableName;
    private final Columns columns;

    EntityLoader(JdbcTemplate jdbcTemplate, Class<T> tClass, Query query) {
        this.query = query;

        this.jdbcTemplate = jdbcTemplate;
        this.resultMapper = new ResultMapper<>(tClass);

        this.tableName = TableName.of(tClass);
        this.columns = Columns.of(tClass.getDeclaredFields());
    }

    public List<T> findAll() {
        String q = query.select(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns);

        return jdbcTemplate.query(q, resultMapper);
    }

    public <I> T findById(I input) {
        String q = query.select(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns, input);

        return jdbcTemplate.queryForObject(q, resultMapper);
    }

    public <I> int getHashCode(I input) {
        return query.select("findById", tableName, columns, input).hashCode();
    }
}
