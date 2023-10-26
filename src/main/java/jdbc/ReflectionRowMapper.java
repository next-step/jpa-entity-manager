package jdbc;

import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityColumn;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionRowMapper<T> implements RowMapper<T> {

    private static final Map<EntityClass<?>, ReflectionRowMapper<?>> CACHE = new ConcurrentHashMap<>();

    private final EntityClass<T> entityClass;

    public ReflectionRowMapper(EntityClass<T> entityClass) {
        this.entityClass = entityClass;
    }

    public static <T> ReflectionRowMapper<T> getInstance(final EntityClass<T> entityClass) {
        return (ReflectionRowMapper<T>) CACHE.computeIfAbsent(entityClass, ReflectionRowMapper::new);
    }

    @Override
    public T mapRow(final ResultSet resultSet) throws SQLException {
        T instance = entityClass.newInstance();
        List<EntityColumn> entityColumns = entityClass.getEntityColumns();
        for (EntityColumn entityColumn : entityColumns) {
            entityColumn.assignFieldValue(instance, resultSet.getObject(entityColumn.getFieldName()));
        }
        return instance;
    }
}
