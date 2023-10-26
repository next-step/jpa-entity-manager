package persistence.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.meta.ColumnType;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class EntityLoader {
    private final EntityMeta entityMeta;
    private final Dialect dialect;
    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(JdbcTemplate jdbcTemplate, EntityMeta entityMeta, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.dialect = dialect;
    }

    public <T> T find(Class<T> tClass, Object id) {
        final String query = QueryGenerator.of(entityMeta, dialect)
                .select()
                .findByIdQuery(id);

        return jdbcTemplate.queryForObject(query,
                (resultSet) -> resultSetToEntity(tClass, resultSet));
    }

    public <T> List<T> findAll(Class<T> tClass) {
        final String query = QueryGenerator.of(entityMeta, dialect)
                .select()
                .findAllQuery();

        return jdbcTemplate.query(query, (resultSet) -> resultSetToEntity(tClass, resultSet));
    }

    private <T> T resultSetToEntity(Class<T> tClass, ResultSet resultSet) {
        final T instance = ReflectionUtils.getInstance(tClass);

        for (EntityColumn entityColumn : entityMeta.getEntityColumns()) {
            final Object resultSetColumn = getLoadValue(resultSet, entityColumn);
            ReflectionUtils.setFieldValue(instance, entityColumn.getFieldName(), resultSetColumn);
        }
        return instance;
    }

    private Object getLoadValue(ResultSet resultSet, EntityColumn column) {
        try {
            return getTypeValue(resultSet, column);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getTypeValue(ResultSet resultSet, EntityColumn column) throws SQLException {
        final ColumnType columType = column.getColumnType();
        if (columType.isBigInt()) {
            return resultSet.getLong(column.getName());
        }
        if (columType.isVarchar()) {
            return resultSet.getString(column.getName());
        }
        if (columType.isInteger()) {
            return resultSet.getInt(column.getName());
        }
        return null;
    }
}
