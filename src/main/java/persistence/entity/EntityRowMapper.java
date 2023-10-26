package persistence.entity;

import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EntityRowMapper<T> {
    private final Class<T> clazz;
    private final List<String> columnNames;
    private final List<String> fieldNames;
    private final int columnSize;

    public EntityRowMapper(final Class<T> clazz, final EntityPersister entityPersister) {
        this.clazz = clazz;
        this.columnNames = entityPersister.getColumnNames();
        this.fieldNames = entityPersister.getColumnFieldNames();
        this.columnSize = entityPersister.getColumnSize();
    }

    public T mapRow(final ResultSet resultSet) {
        try {
            final T instance = ReflectionUtils.createInstance(clazz);
            for (int i = 0; i < columnSize; i++) {
                final String fieldName = fieldNames.get(i);
                final String columnName = columnNames.get(i);
                final Object object;
                object = resultSet.getObject(columnName);
                ReflectionUtils.injectField(instance, fieldName, object);
            }
            return instance;
        } catch (final SQLException e) {
            throw new PersistenceException("ResultSet Mapping 중 에러가 발생했습니다.", e);
        }
    }
}