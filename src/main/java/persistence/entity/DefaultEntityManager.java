package persistence.entity;


import java.util.List;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;


public class DefaultEntityManager implements EntityManager {
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final EntityMeta entityMeta;
    private final PersistenceContext persistenceContext;

    public DefaultEntityManager(JdbcTemplate jdbcTemplate, EntityMeta entityMeta, Dialect dialect) {
        final QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
        this.entityMeta = entityMeta;
        this.persistenceContext = new DefaultPersistenceContext(entityMeta);
        this.entityLoader = new EntityLoader(jdbcTemplate, entityMeta, queryGenerator);
        this.entityPersister = new EntityPersister(jdbcTemplate, entityMeta, queryGenerator);
    }

    @Override
    public <T> T persist(T entity) {
        final T persistEntity = entityPersister.insert(entity);
        persistenceContext.addEntity(getCacheKey(persistEntity), persistEntity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        Object cacheKey = getCacheKey(entity);

        if (persistenceContext.getEntity(cacheKey) != null) {
            persistenceContext.removeEntity(entity);
        }
        entityPersister.delete(entity);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        if (persistenceContext.getEntity(id) != null) {
            return (T) persistenceContext.getEntity(id);
        }

        final T entity = entityLoader.find(clazz, id);
        if (entity != null) {
            persistenceContext.addEntity(getCacheKey(entity), entity);
        }
        return entity;
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        final List<T> findList = entityLoader.findAll(tClass);
        findList.forEach((it) ->
                persistenceContext.addEntity(getCacheKey(it), it)
        );
        return findList;
    }

    private <T> Object getCacheKey(T persistEntity) {
        return entityMeta.getPkValue(persistEntity);
    }


}
