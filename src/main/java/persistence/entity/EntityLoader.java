package persistence.entity;

import database.sql.dml.SelectOneQueryBuilder;
import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;
import jdbc.RowMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

// XXX: test
public class EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final Class<?> entityClass;
    private final EntityMetadata entityMetadata;
    private final SelectOneQueryBuilder selectOneQueryBuilder;
    private final RowMapper<Object> rowMapper = rowMapper();

    public EntityLoader(JdbcTemplate jdbcTemplate, Class<?> entityClass) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityClass = entityClass;
        this.entityMetadata = new EntityMetadata(this.entityClass);
        this.selectOneQueryBuilder = new SelectOneQueryBuilder(entityClass);
    }

    // XXX: SELECT 쿼리 빌더로 변경하기
    public <T> List<T> load(Collection<Long> ids) {
        return ids.stream()
                .map(id -> {
                         String query = selectOneQueryBuilder.buildQuery(id);
                         jdbcTemplate.queryForObject(query, rowMapper);
                         return (T) jdbcTemplate.queryForObject(query, rowMapper);
                     }
                )
                .collect(Collectors.toList());
    }

    public <T> T load(Long id) {
        String query = selectOneQueryBuilder.buildQuery(id);
        return (T) jdbcTemplate.queryForObject(query, rowMapper);
    }

    // XXX: 별도 클래스 분리?
    private RowMapper<Object> rowMapper() {
        return resultSet -> {
            ResultSetMetaData rsMetaData = resultSet.getMetaData();
            try {
                Constructor<?> declaredConstructor = entityClass.getDeclaredConstructor();
                Object object = declaredConstructor.newInstance(); // XXX <- 캐스팅

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
        Field field = entityMetadata.getFieldByColumnName(columnName);
        field.setAccessible(true);

        Object value;

        switch (columnType) {
            case -5: // Long
                value = resultSet.getLong(columnName);
                field.set(entity, value);
                break;
            case 4: // Int
                value = resultSet.getInt(columnName);
                field.set(entity, value);
                break;
            case 12: // String
                value = resultSet.getString(columnName);
                field.set(entity, value);
                break;
            default:
                throw new UnsupportedOperationException("아지 변환 지원 안하는 타입입나다: " + columnType);
        }
        field.set(entity, value);
    }
}
