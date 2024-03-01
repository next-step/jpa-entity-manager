package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;
import persistence.sql.meta.pk.PKField;

import java.lang.reflect.Field;

public class EntityPersist {
    private final JdbcTemplate jdbcTemplate;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersist(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.updateQueryBuilder = new UpdateQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
    }

    public <T> void insert(T entity) {
        jdbcTemplate.execute(insertQueryBuilder.generateSQL(entity));
    }

    public <T> void update(Long pk, T entity) {
        String sql = updateQueryBuilder.generateSQL(pk, entity);
        jdbcTemplate.execute(sql);
    }

    public <T> void delete(T entity) {
        jdbcTemplate.execute(deleteQueryBuilder.generateSQL(entity));
    }

    // TODO 해당 코드 리팩토링 필요
    public <T> Long getPKValue(T entity) {
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

}
