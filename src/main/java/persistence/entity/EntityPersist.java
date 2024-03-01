package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapperImpl;
import persistence.sql.dml.builder.DeleteQueryBuilder;
import persistence.sql.dml.builder.InsertQueryBuilder;
import persistence.sql.dml.builder.SelectQueryBuilder;
import persistence.sql.dml.builder.UpdateQueryBuilder;
import persistence.sql.meta.pk.PKField;

import java.lang.reflect.Field;
import java.util.List;

public class EntityPersist {
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;
    private final InsertQueryBuilder insertQueryBuilder;
    private final UpdateQueryBuilder updateQueryBuilder;
    private final DeleteQueryBuilder deleteQueryBuilder;

    public EntityPersist(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = new SelectQueryBuilder();
        this.insertQueryBuilder = new InsertQueryBuilder();
        this.updateQueryBuilder = new UpdateQueryBuilder();
        this.deleteQueryBuilder = new DeleteQueryBuilder();
    }

    public <T> T findOneOrFail(Class<T> clazz, Long id) {
        return jdbcTemplate.queryForObject(selectQueryBuilder.findById(clazz, id), new RowMapperImpl<>(clazz));
    }

    public <T> void insert(T entity) {
        jdbcTemplate.execute(insertQueryBuilder.generateSQL(entity));
    }

    public <T> void update(Long pk, T findEntity, T entity) {
        String sql = updateQueryBuilder.generateSQL(pk, findEntity, entity);
        jdbcTemplate.execute(sql);
    }

    public <T> T findOne(T entity, Long id) {
        List<T> entities = (List<T>) jdbcTemplate.query(
                selectQueryBuilder.findById(entity.getClass(), id),
                new RowMapperImpl<>(entity.getClass())
        );
        if (entities.isEmpty()) {
            return null;
        }
        return entities.get(0);
    }

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

    public <T> void delete(T entity) {
        jdbcTemplate.execute(deleteQueryBuilder.generateSQL(entity));
    }
}
