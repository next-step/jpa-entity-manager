package persistence.entity;

import java.util.List;
import jdbc.JdbcTemplate;
import jdbc.ResultMapper;
import persistence.sql.QueryUtil;
import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

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
        String query = QueryUtil.select().get(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns);

        return jdbcTemplate.query(query, resultMapper);
    }

    public <I> T findById(I input) {
        String query = QueryUtil.select().get(new Object() {
        }.getClass().getEnclosingMethod().getName(), tableName, columns, input);

        return jdbcTemplate.queryForObject(query, resultMapper);
    }

    public <I> boolean update(I input, Object arg) {
        try {
            String query = QueryUtil.update().get(getValues(input), tableName, columns, arg);

            jdbcTemplate.execute(query);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <I> void insert(I input) {
        String query = QueryUtil.insert().get(tableName, columns, getValues(input));

        jdbcTemplate.execute(query);
    }

    public void delete(Object arg) {
        String query = QueryUtil.delete().get(tableName, columns, arg);

        jdbcTemplate.execute(query);
    }

    public <I> Values getValues(I input) {
        return Values.of(input);
    }
}
