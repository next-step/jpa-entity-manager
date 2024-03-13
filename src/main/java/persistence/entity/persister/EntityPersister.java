package persistence.entity.persister;

import jakarta.persistence.Id;
import jdbc.JdbcTemplate;
import persistence.entity.exception.UnableToChangeIdException;
import persistence.sql.common.DtoMapper;
import persistence.sql.ddl.PrimaryKeyClause;
import persistence.sql.dml.DeleteQueryBuilder;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.UpdateQueryBuilder;

import java.util.Arrays;

public class EntityPersister {
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Object update(Object entity, Long id) {
        String query = new UpdateQueryBuilder(entity.getClass()).getQuery(entity, id);
        jdbcTemplate.executeUpdate(query);
        return entity;
    }

    public Object insert(Object entity) {
        Class<?> clazz = entity.getClass();
        String queryToInsert = new InsertQueryBuilder(clazz).getInsertQuery(entity);
        Long id = jdbcTemplate.execute(queryToInsert);

        initId(entity, id);
        return entity;
    }

    private void initId(Object entity, Long id) {
        var idField = Arrays.stream(entity.getClass().getDeclaredFields()).filter(x -> x.isAnnotationPresent(Id.class)).findAny().get();
        idField.setAccessible(true);
        try {
            idField.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new UnableToChangeIdException();
        }
    }
    public void delete(Object entity) {
        Long id = PrimaryKeyClause.primaryKeyValue(entity);
        String query = new DeleteQueryBuilder(entity.getClass()).deleteById(id);
        jdbcTemplate.execute(query);
    }
}
