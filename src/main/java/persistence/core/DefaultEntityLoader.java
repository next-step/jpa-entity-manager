package persistence.core;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.entity.metadata.EntityMetadata;
import persistence.sql.dml.DMLQueryBuilder;

public class DefaultEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaManager entityMetaManager;
    private final DMLQueryBuilder dmlQueryBuilder;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaManager = EntityMetaManager.getInstance();
        this.dmlQueryBuilder = DMLQueryBuilder.getInstance();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        try {
            EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(clazz);
            String sql = dmlQueryBuilder.selectByIdQuery(entityMetadata.getTableName(), entityMetadata.getColumns(), id);

            return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(clazz));
        } catch (RuntimeException e) {
            throw new RuntimeException("Entity not found", e);
        }
    }
}
