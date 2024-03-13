package persistence.core;

import jdbc.JdbcTemplate;
import persistence.entity.metadata.EntityMetadata;
import persistence.sql.dml.DMLQueryBuilder;

public class DefaultEntityPersister implements EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final EntityMetaManager entityMetaManager;
    private final DMLQueryBuilder dmlQueryBuilder;

    public DefaultEntityPersister(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMetaManager = EntityMetaManager.getInstance();
        this.dmlQueryBuilder = DMLQueryBuilder.getInstance();
    }

    @Override
    public Long insert(Object entity) {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(entity.getClass());
        String sql = dmlQueryBuilder.insertSql(entityMetadata.getTableName(), entityMetadata.getColumns(), entityMetadata.getColumnValues(entity));

        return (Long) jdbcTemplate.execute(sql);
    }

    @Override
    public boolean update(Object entity) {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(entity.getClass());
        String sql = dmlQueryBuilder.updateSql(entityMetadata.getTableName(), entityMetadata.getColumns(), entityMetadata.getColumnValues(entity));
        jdbcTemplate.execute(sql);

        return true;
    }

    @Override
    public void delete(Object entity) {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(entity.getClass());
        String sql = dmlQueryBuilder.deleteSql(entityMetadata.getTableName(), entityMetadata.getColumns(), entityMetadata.getColumnValues(entity));
        jdbcTemplate.execute(sql);
    }

    @Override
    public void setIdentifier(Object entity, Object id) {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(entity.getClass());
        entityMetadata.setValue(entity, entityMetadata.getIdColumn().getColumnName(), id);
    }

    public Long getIdentifier(Object entity) {
        EntityMetadata entityMetadata = entityMetaManager.getEntityMetadata(entity.getClass());
        return (Long) entityMetadata.getIdColumn().getValue(entity);
    }

}
