package persistence.persister;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.dml.FindByIdQueryBuilder;
import persistence.sql.dml.RowDeleteQueryBuilder;
import persistence.sql.dml.RowInsertQueryBuilder;
import persistence.sql.dml.RowUpdateQueryBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityPersister {
    private static final Logger logger = LoggerFactory.getLogger(EntityPersister.class);

    private final Class<?> clazz;
    private final EntityName entityName;
    private final EntityTableName entityTableName;
    private final EntityColumns entityColumns;
    private final EntityPrimaryKey entityPrimaryKey;

    public EntityPersister(Class<?> clazz) {
        this.clazz = clazz;
        this.entityName = new EntityName(clazz);
        this.entityTableName = new EntityTableName(clazz);
        this.entityColumns = new EntityColumns(clazz);
        this.entityPrimaryKey = new EntityPrimaryKey(clazz);
    }

    public boolean update(Object object, JdbcTemplate jdbcTemplate) {
        RowUpdateQueryBuilder queryBuilder = new RowUpdateQueryBuilder();
        String query = queryBuilder.generateSQLQuery(
            this.entityTableName.getName(),
            this.entityColumns.getColumnNames(),
            this.entityColumns.getColumnValues(object),
            this.entityPrimaryKey.getPrimaryKeyName(),
            this.entityPrimaryKey.getPrimaryKeyValue(object)
        );
        logger.info(query);

        try {
            jdbcTemplate.execute(query);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public void insert(Object object, JdbcTemplate jdbcTemplate) {
        RowInsertQueryBuilder queryBuilder = new RowInsertQueryBuilder();
        String query = queryBuilder.generateSQLQuery(
            this.entityTableName.getName(),
            this.entityColumns.getColumnNames(),
            this.entityColumns.getColumnValues(object),
            this.entityPrimaryKey.getPrimaryKeyName(),
            this.entityPrimaryKey.getPrimaryKeyValue(object)
        );
        logger.info(query);

        Object value = jdbcTemplate.queryForKey(query);
        this.entityPrimaryKey.setPrimaryKey(object, value);
    }

    public Object find(Object primaryKevValue, JdbcTemplate jdbcTemplate) {
        FindByIdQueryBuilder queryBuilder = new FindByIdQueryBuilder();
        String query = queryBuilder.generateSQLQuery(
            this.entityTableName.getName(),
            this.entityColumns.getColumnNames(),
            this.entityPrimaryKey.getPrimaryKeyName(),
            primaryKevValue
        );
        logger.info(query);

        return jdbcTemplate.queryForObject(query, resultSet -> {
            if (!resultSet.next()) {
                return null;
            }

            try {
                Object result = getEmptyObject(this.clazz);
                Object primaryKeyValue = resultSet.getObject(this.entityPrimaryKey.getPrimaryKeyName());
                this.entityPrimaryKey.setPrimaryKey(result, primaryKeyValue);

                for (String columnName : this.entityColumns.getColumnNames()) {
                    Object value = resultSet.getObject(columnName);
                    if (value == null) {
                        continue;
                    }

                    Field field = this.entityColumns.getField(columnName);
                    field.set(result, value);
                }

                return this.clazz.cast(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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

    public void delete(Object object, JdbcTemplate jdbcTemplate) {
        RowDeleteQueryBuilder queryBuilder = new RowDeleteQueryBuilder();
        String query = queryBuilder.generateSQLQuery(
            this.entityTableName.getName(),
            this.entityPrimaryKey.getPrimaryKeyName(),
            this.entityPrimaryKey.getPrimaryKeyValue(object)
        );
        logger.info(query);

        jdbcTemplate.execute(query);
    }
}
