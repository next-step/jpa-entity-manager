package persistence.loader;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.persister.EntityColumns;
import persistence.persister.EntityName;
import persistence.persister.EntityPrimaryKey;
import persistence.persister.EntityTableName;
import persistence.sql.dml.FindByIdQueryBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityLoader {
    private static final Logger logger = LoggerFactory.getLogger(EntityLoader.class);

    private final EntityName entityName;
    private final EntityTableName entityTableName;
    private final EntityColumns entityColumns;
    private final EntityPrimaryKey entityPrimaryKey;
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper rowMapper;

    public EntityLoader(Class<?> clazz, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityName = new EntityName(clazz);
        this.entityTableName = new EntityTableName(clazz);
        this.entityColumns = new EntityColumns(clazz);
        this.entityPrimaryKey = new EntityPrimaryKey(clazz);
        this.rowMapper = resultSet -> {
            if (!resultSet.next()) {
                throw new RuntimeException("entity not found");
            }

            try {
                Object result = getEmptyObject(clazz);
                Object primaryKeyValue = resultSet.getObject(entityPrimaryKey.getPrimaryKeyName());
                entityPrimaryKey.setPrimaryKey(result, primaryKeyValue);

                for (String columnName : entityColumns.getColumnNames()) {
                    Object value = resultSet.getObject(columnName);
                    if (value == null) {
                        continue;
                    }

                    Field field = entityColumns.getColumnField(columnName);
                    field.set(result, value);
                }

                return clazz.cast(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public Object find(Object primaryKevValue) {
        String query = FindByIdQueryBuilder.generateSQLQuery(
            this.entityTableName.getName(),
            this.entityColumns.getColumnNames(),
            this.entityPrimaryKey.getPrimaryKeyName(),
            primaryKevValue
        );
        logger.info(query);

        return jdbcTemplate.queryForObject(query, rowMapper);
    }

    private Object getEmptyObject(Class<?> clazz) {
        Constructor<?> emptyConstructor = Arrays
            .stream(clazz.getDeclaredConstructors())
            .filter(constructor -> constructor.getParameterCount() == 0)
            .findFirst()
            .orElse(null);
        if (emptyConstructor == null) {
            throw new RuntimeException("Entity must have empty constructor!");
        }

        try {
            return emptyConstructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
