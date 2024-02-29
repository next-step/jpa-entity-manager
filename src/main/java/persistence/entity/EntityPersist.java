package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;
import persistence.sql.meta.column.ColumnName;
import persistence.sql.meta.pk.PKField;
import persistence.sql.meta.table.TableName;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityPersist {
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersist(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = new SelectQueryBuilder();
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
    }

    public <T> T findOneOrFail(Class<T> clazz, Long id) {
        return jdbcTemplate.queryForObject(selectQueryBuilder.findById(clazz, id), new RowMapperImpl<>(clazz));
    }

    public <T> void persist(T entity) {
        Long pkValue = getPKValue(entity);
        if (pkValue == null) {
            insert(entity);
            return;
        }
        T findEntity = findOne(entity, pkValue);
        if (findEntity == null) {
            insert(entity);
            return;
        }
        update(pkValue, findEntity, entity);
    }

    private <T> void insert(T entity) {
        jdbcTemplate.execute(insertQueryBuilder.generateSQL(entity));
    }

    private <T> void update(Long pk, T findEntity, T entity) {
        Class<?> clazz = findEntity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        Arrays.stream(fields)
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        Object findValue = field.get(findEntity);
                        Object newValue = field.get(entity);
                        if (findValue != newValue) {
                            sb.append(new ColumnName(field).getName() + "=" + "'" + newValue.toString() + "'");
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
        TableName tableName = new TableName(findEntity.getClass());
        String sql = String.format("UPDATE %s SET %s WHERE id = %s",
                tableName.getName(), sb, pk.toString());
        jdbcTemplate.execute(sql);
    }

    private <T> T findOne(T entity, Long id) {
        List<T> entities = (List<T>) jdbcTemplate.query(
                selectQueryBuilder.findById(entity.getClass(), id),
                new RowMapperImpl<>(entity.getClass())
        );
        if (entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

    private <T> Long getPKValue(T entity) {
        Field pkField = new PKField(entity.getClass()).getField();
        pkField.setAccessible(true);
        Long value;
        try {
            value = (Long) pkField.get(entity);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        return value;
    }

    public <T> void delete(T entity) {
        jdbcTemplate.execute(deleteQueryBuilder.generateSQL(entity));
    }
}
