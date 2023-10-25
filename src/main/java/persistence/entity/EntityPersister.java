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
    private final TableName tableName;
    private final Columns columns;

    public EntityPersister(JdbcTemplate jdbcTemplate, Class<T> tClass) {
        this.jdbcTemplate = jdbcTemplate;

        this.tableName = TableName.of(tClass);
        this.columns = Columns.of(tClass.getDeclaredFields());
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
