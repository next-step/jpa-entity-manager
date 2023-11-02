package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.SimpleEntityRowMapper;
import persistence.sql.dbms.Dialect;
import persistence.sql.dml.SelectDMLQueryBuilder;
import persistence.sql.dml.clause.WhereClause;
import persistence.sql.dml.clause.operator.Operator;
import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityTable;

public class JdbcEntityManager implements EntityManager {
    private JdbcTemplate jdbcTemplate;
    private Dialect dialect;
    private EntityManagementCache entityPersisterCache;

    public JdbcEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.jdbcTemplate = jdbcTemplate;
        this.dialect = dialect;
        this.entityPersisterCache = new EntityManagementCache(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityLoader<T> entityLoader = entityPersisterCache.loader(clazz);

        return entityLoader.findById(clazz, id);
    }

    public <T> T findByEntity(T entity) {
        EntityLoader<T> entityLoader = (EntityLoader<T>) entityPersisterCache.loader(entity.getClass());

        return entityLoader.findOne(entity);
    }

    @Override
    public void persist(Object entity) {
        EntityPersister entityPersister = entityPersisterCache.persister(entity.getClass());
        Object persistedEntity = findByEntity(entity);

        if (persistedEntity != null) {
            entityPersister.update(entity);
            return;
        }

        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        EntityPersister entityPersister = entityPersisterCache.persister(entity.getClass());

        entityPersister.delete(entity);
    }
}
