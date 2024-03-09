package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.ddl.exception.IdAnnotationMissingException;
import persistence.sql.dml.*;
import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;
import jdbc.JdbcTemplate;
import persistence.sql.mapping.TableData;

import java.lang.reflect.Field;
import java.util.Arrays;

import static persistence.sql.dml.BooleanExpression.eq;

public class EntityPersisterImpl implements EntityPersister {
    private final GeneratedIdObtainStrategy generatedIdObtainStrategy;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersisterImpl(GeneratedIdObtainStrategy generatedIdObtainStrategy, JdbcTemplate jdbcTemplate) {
        this.generatedIdObtainStrategy = generatedIdObtainStrategy;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean update(Object entity) {
        TableData table = TableData.from(entity.getClass());
        Columns columns = Columns.createColumnsWithValue(entity);
        ColumnData keyColumn = columns.getKeyColumn();

        if(keyColumn.getValue() == null) {
            return false;
        }

        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(table, columns);
        WhereBuilder whereBuilder = new WhereBuilder();
        whereBuilder.and(eq(keyColumn.getName(), keyColumn.getValue()));

        jdbcTemplate.execute(updateQueryBuilder.build(entity, whereBuilder));

        return true;
    }

    @Override
    public void insert(Object entity) {
        Class<?> clazz = entity.getClass();
        InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder(clazz);

        jdbcTemplate.execute(insertQueryBuilder.build(entity));

        setIdToEntity(entity, clazz);
    }

    private void setIdToEntity(Object entity, Class<?> clazz) {
        Object generatedId = jdbcTemplate.queryForObject(
                generatedIdObtainStrategy.getQueryString(),
                generatedIdObtainStrategy.getRowMapper()
        );

        Field idField = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow();
        idField.setAccessible(true);

        try {
            idField.set(entity, generatedId);
        } catch (IllegalAccessException e) {
            throw new IdAnnotationMissingException();
        }
    }


    @Override
    public void delete(Object entity) {
        Class<?> clazz = entity.getClass();
        ColumnData idColumn = Columns.createColumnsWithValue(entity).getKeyColumn();

        DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder(clazz);
        WhereBuilder builder = new WhereBuilder();
        builder.and(eq(idColumn.getName(), idColumn.getValue()));

        jdbcTemplate.execute(deleteQueryBuilder.build(builder));
    }
}
