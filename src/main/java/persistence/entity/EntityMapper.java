package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import persistence.exception.NotFoundException;
import persistence.meta.ColumnType;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;

public class EntityMapper {
    private final EntityMeta entityMeta;

    public EntityMapper(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    public <T> T resultSetToEntity(Class<T> tClass, ResultSet resultSet) {
        final T instance = getInstance(tClass);
        for (EntityColumn entityColumn : entityMeta.getEntityColumns()) {
            final Object resultSetColumn = getResultSetColumn(resultSet, entityColumn);
            final Field field = getFiled(tClass, entityColumn);
            setFieldValue(instance, field, resultSetColumn);
        }
        return instance;
    }

    private <T> void setFieldValue(T instance, Field field, Object resultSetColumn) {
        try {
            field.setAccessible(true);
            field.set(instance, resultSetColumn);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T getInstance(Class<T> tClass) {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            throw new NotFoundException(e);
        }
    }

    private <T> Field getFiled(Class<T> tClass, EntityColumn entityColumn) {
        try {
            return tClass.getDeclaredField(entityColumn.getFieldName());
        } catch (NoSuchFieldException e) {
            throw new NotFoundException("필드를 찾을수 없습니다.");
        }

    }

    private Object getResultSetColumn(ResultSet resultSet, EntityColumn column) {
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
