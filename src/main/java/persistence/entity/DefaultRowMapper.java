package persistence.entity;

import jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.meta.EntityField;
import persistence.sql.meta.EntityTable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultRowMapper<T> implements RowMapper<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRowMapper.class);

    private final Class<T> clazz;

    DefaultRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        final T entity = new InstanceFactory<>(clazz).createInstance();
        new EntityTable(clazz).getEntityFields()
                .forEach(entityField -> mapField(resultSet, entityField, entity));
        return entity;
    }

    private void mapField(ResultSet resultSet, EntityField entityField, T entity) {
        try {
            final Object value = resultSet.getObject(entityField.getColumnName(), entityField.getType());
            entityField.setValue(entity, value);
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
