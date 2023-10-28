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
        final Object id = getEntityId(persistEntity);
        initSavePersistContext(id, persistEntity);
        return persistEntity;
    }

    @Override
    public void remove(Object entity) {
        Object cacheKey = getEntityId(entity);
        if (persistenceContext.getEntity(cacheKey) != null) {
            persistenceContext.removeEntity(entity);
        }
        entityPersister.delete(entity);
    }

    @Override
    public <T, ID> T find(Class<T> clazz, ID id) {
        if (persistenceContext.getEntity(id) != null) {
            return (T) persistenceContext.getEntity(id);
        }

        final T entity = entityLoader.find(clazz, id);
        if (entity != null) {
            initSavePersistContext(id, entity);
        }

        return (T) persistenceContext.getEntity(id);
    }

    private <T> void initSavePersistContext(Object id, T entity) {
        persistenceContext.getDatabaseSnapshot(id, entity);
        persistenceContext.addEntity(id, entity);

    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        final List<T> findList = entityLoader.findAll(tClass);
        findList.forEach((it) -> {
            Object id = getEntityId(it);
            persistenceContext.addEntity(id, it);
            persistenceContext.getDatabaseSnapshot(id, it);
        });
        return findList;
    }

    private <T> Object getEntityId(T persistEntity) {
        return entityMeta.getPkValue(persistEntity);
    }

    @Override
    public void flush() {
        final List<Object> changedEntity = persistenceContext.getChangedEntity();

        changedEntity.forEach(entityPersister::update);

        persistenceContext.clear();
    }
}
