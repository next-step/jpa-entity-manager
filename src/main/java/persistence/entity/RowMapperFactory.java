package persistence.entity;

import database.sql.util.EntityMetadata;
import jdbc.RowMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static java.sql.Types.*;

class RowMapperFactory {
    private RowMapperFactory() {
    }

    public static RowMapper<Object> from(Class<?> entityClass) {
        EntityMetadata entityMetadata = new EntityMetadata(entityClass);
        return rowMapper(entityClass, entityMetadata);
    }

    private static RowMapper<Object> rowMapper(Class<?> entityClass, EntityMetadata entityMetadata) {
        return resultSet -> {
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            try {
                Constructor<?> declaredConstructor = entityClass.getDeclaredConstructor();
                Object object = declaredConstructor.newInstance();

                for (int i = 1; i < rsMetaData.getColumnCount() + 1; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    int columnType = rsMetaData.getColumnType(i);
                    try {
                        getSet(resultSet, columnName, columnType, object, entityMetadata);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                return object;
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                     InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        };
    }

    // XXX: 이름
    private static void getSet(ResultSet resultSet, String columnName, int columnType, Object entity,
                               EntityMetadata entityMetadata) throws SQLException, IllegalAccessException {
        Object value;
        // TODO: JavaTypes 사용해서 타입 관련 코드 개선
        switch (columnType) {
            case BIGINT:
                value = resultSet.getLong(columnName);
                break;
            case INTEGER:
                value = resultSet.getInt(columnName);
                break;
            case VARCHAR:
                value = resultSet.getString(columnName);
                break;
            default:
                throw new UnsupportedOperationException("아직 변환 지원 안하는 타입입나다: " + columnType);
        }

        Field field = entityMetadata.getFieldByColumnName(columnName);
        field.setAccessible(true);
        field.set(entity, value);
    }
}
