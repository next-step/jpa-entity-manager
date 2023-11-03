package persistence.entity;

import jakarta.persistence.Transient;
import java.util.Arrays;
import jdbc.JdbcTemplate;
import persistence.sql.dml.Query;
import persistence.sql.common.instance.Values;
import persistence.sql.common.meta.Columns;
import persistence.sql.common.meta.TableName;

import java.util.List;

public class EntityPersister<T> {
    private final Query query;
    private final EntityLoader<T> entityLoader;

    private final JdbcTemplate jdbcTemplate;
    private final TableName tableName;
    private final Columns columns;

    public EntityPersister(JdbcTemplate jdbcTemplate, Class<T> tClass, Query query) {
        this.jdbcTemplate = jdbcTemplate;

        this.tableName = TableName.of(tClass);
        this.columns = Columns.of(tClass.getDeclaredFields());
        this.query = query;

        this.entityLoader = new EntityLoader<>(jdbcTemplate, tClass, query);
    }

    public List<T> findAll() {
        return entityLoader.findAll();
    }

    public <I> T findById(I input) {
        return entityLoader.findById(input);
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

    public <I> int getHashCode(I input) {
        return entityLoader.getHashCode(input);
    }

    public <I> String getIdValue(I input) {
        return getValues(input).getValue(columns.getIdFieldName());
    }

    public <I> T getEntity(I input) {
        T destination;
        try {
            destination = (T) input.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Arrays.stream(input.getClass().getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .forEach(field -> {
                field.setAccessible(true);
                try {
                    field.set(destination, field.get(input));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });

        return destination;
    }

    private <I> Values getValues(I input) {
        return Values.of(input);
    }
}
