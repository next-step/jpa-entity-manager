package persistence.entity.persister;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.PrimaryKey;
import persistence.entity.exception.OptionalForbiddenException;
import persistence.entity.exception.UnableToChangeIdException;
import persistence.sql.dml.querybuilder.DeleteQueryBuilder;
import persistence.sql.dml.querybuilder.InsertQueryBuilder;
import persistence.sql.dml.querybuilder.UpdateQueryBuilder;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T update(T entity, Long id) {
        if (entity instanceof Optional) {
            throw new OptionalForbiddenException();
        }
        String query = new UpdateQueryBuilder(entity.getClass()).getQuery(entity, id);
        jdbcTemplate.executeUpdate(query);
        return entity;
    }

    public <T> T insert(T entity) {
        if (entity instanceof Optional) {
            throw new OptionalForbiddenException();
        }
        Class<?> clazz = entity.getClass();
        String queryToInsert = new InsertQueryBuilder(clazz).getInsertQuery(entity);
        Long id = jdbcTemplate.executeUpdate(queryToInsert);
        initId(entity, id);
        return entity;
    }

    private void initId(Object entity, Long id) {
        Field idField = Arrays.stream(entity.getClass().getDeclaredFields()).filter(x -> x.isAnnotationPresent(Id.class)).findAny().get();
        idField.setAccessible(true);
        try {
            idField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new UnableToChangeIdException();
        }
    }
    public void delete(Object entity) {
        Long id = new PrimaryKey(entity).value();
        String query = new DeleteQueryBuilder(entity.getClass()).deleteById(id);
        jdbcTemplate.execute(query);
    }
}
