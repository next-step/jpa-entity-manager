package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.QueryException;

import java.lang.reflect.Field;
import java.util.Arrays;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;

    public SimpleEntityManager(final EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long id) {
//        final Table table = tableBinder.createTable(clazz);
//
//        final Field idField = findIdField(clazz);
//        final Column idColumn = table.getColumn(ColumnBinder.toColumnName(idField));
//        idColumn.getValue().setValue(1L);
//
//        final Where where = new Where(idColumn, idColumn.getValue(), LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ));
//        final Select select = new Select(table, List.of(where));
//
//        final String selectQuery = dmlQueryBuilder.buildSelectQuery(select);
//        final EntityRowMapper<T> entityRowMapper = new EntityRowMapper<>(clazz);
//
//        return jdbcTemplate.queryForObject(selectQuery, entityRowMapper);
        return null;
    }

    private <T> Field findIdField(final Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new QueryException("not found id column"));
    }

    @Override
    public void persist(final Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(final Object entity) {
        entityPersister.delete(entity);
    }

}
