package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.Query;
import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

public class EntityPersister<T> {
    private final Query query;

    private final JdbcTemplate jdbcTemplate;
    private final TableName tableName;
    private final Columns columns;

    public EntityPersister(JdbcTemplate jdbcTemplate, Class<T> tClass, Query query) {
        this.jdbcTemplate = jdbcTemplate;

        this.tableName = TableName.of(tClass);
        this.columns = Columns.of(tClass.getDeclaredFields());
        this.query = query;
    }

    public <I> boolean update(I input, Object arg) {
        try {
            String q = query.update(getValues(input), tableName, columns, arg);

            jdbcTemplate.execute(q);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public <I> void insert(I input) {
        String q = query.insert(tableName, columns, getValues(input));

        jdbcTemplate.execute(q);
    }

    public void delete(Object arg) {
        String q = query.delete(tableName, columns, arg);

        jdbcTemplate.execute(q);
    }

    public <I> Values getValues(I input) {
        return Values.of(input);
    }
}
