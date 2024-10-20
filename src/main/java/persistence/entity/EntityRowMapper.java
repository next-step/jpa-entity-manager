package persistence.entity;

import jdbc.RowMapper;
import persistence.sql.Queryable;
import persistence.sql.definition.TableDefinition;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;

    public EntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            final TableDefinition tableDefinition = new TableDefinition(clazz);

            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Queryable field : tableDefinition.withIdColumns()) {
                final String databaseColumnName = field.getName();
                final Field objectDeclaredField = clazz.getDeclaredField(field.getDeclaredName());

                final boolean wasAccessible = objectDeclaredField.canAccess(instance);
                if (!wasAccessible) {
                    objectDeclaredField.setAccessible(true);
                }

                objectDeclaredField.set(instance, resultSet.getObject(databaseColumnName));

                if (!wasAccessible) {
                    objectDeclaredField.setAccessible(false);
                }
            }

            return instance;
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Failed to map row to " + clazz.getName(), e);
        }
    }
}
