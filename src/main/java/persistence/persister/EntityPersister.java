package persistence.persister;

import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.dml.RowDeleteQueryBuilder;
import persistence.sql.dml.RowInsertQueryBuilder;
import persistence.sql.dml.RowUpdateQueryBuilder;

public class EntityPersister {
    private static final Logger logger = LoggerFactory.getLogger(EntityPersister.class);

    private final Class<?> clazz;
    private final EntityName entityName;
    private final EntityTableName entityTableName;
    private final EntityColumns entityColumns;
    private final EntityPrimaryKey entityPrimaryKey;
    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(Class<?> clazz, JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.clazz = clazz;
        this.entityName = new EntityName(clazz);
        this.entityTableName = new EntityTableName(clazz);
        this.entityColumns = new EntityColumns(clazz);
        this.entityPrimaryKey = new EntityPrimaryKey(clazz);
    }

    public boolean update(Object object) {
        String query = RowUpdateQueryBuilder.generateSQLQuery(
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

    public void insert(Object object) {
        String query = RowInsertQueryBuilder.generateSQLQuery(
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

    public void delete(Object object) {
        String query = RowDeleteQueryBuilder.generateSQLQuery(
            this.entityTableName.getName(),
            this.entityPrimaryKey.getPrimaryKeyName(),
            this.entityPrimaryKey.getPrimaryKeyValue(object)
        );
        logger.info(query);

        jdbcTemplate.execute(query);
    }
}
