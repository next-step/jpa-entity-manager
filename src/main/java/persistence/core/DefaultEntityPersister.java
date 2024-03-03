package persistence.core;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.entity.EntityValue;
import persistence.entity.metadata.DefaultEntityMetadataReader;
import persistence.entity.metadata.EntityMetadataReader;
import persistence.sql.dml.DMLQueryBuilder;

import java.sql.SQLException;

public class DefaultEntityPersister implements EntityPersister {

    private final EntityMetadataReader entityMetadataReader;
    private final EntityValue entityValue;
    private final DMLQueryBuilder dmlQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    public DefaultEntityPersister(JdbcTemplate jdbcTemplate) throws SQLException {
        this.entityMetadataReader = new DefaultEntityMetadataReader();
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = DMLQueryBuilder.getInstance();
        this.entityValue = new EntityValue(entityMetadataReader);
    }

    @Override
    public void insert(Object entity) {
        String sql = dmlQueryBuilder.insertSql(entity);
        jdbcTemplate.execute(sql);
    }

    @Override
    public <T> T select(Class<?> entityClass, Long id) throws Exception {
        String sql = dmlQueryBuilder.selectByIdQuery(entityClass, id);
        Object o = jdbcTemplate.queryForObject(sql, new DefaultRowMapper<T>((Class<T>) entityClass));

        return (T) o;
    }

    @Override
    public boolean update(Object entity) throws Exception {
        Long idValue = getIdValue(entity);
        try {
            select(entity.getClass(), idValue);
        } catch (Exception e) {
            throw e;
        }

        String sql = dmlQueryBuilder.updateSql(entity);
        jdbcTemplate.execute(sql);

        return true;
    }

    @Override
    public void delete(Object entity) throws Exception {
        Object idValue = getIdValue(entity);
        try {
            select(entity.getClass(), (Long) idValue);
        } catch (Exception e) {
            throw e;
        }

        String sql = dmlQueryBuilder.deleteSql(entity);
        jdbcTemplate.execute(sql);
    }

    private Long getIdValue(Object entity) {
        String idColumnName = entityMetadataReader.getIdColumnName(entity.getClass());

        return entityValue.getValue(entity, idColumnName);
    }
}
