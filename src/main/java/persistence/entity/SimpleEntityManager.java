package persistence.entity;


import java.util.List;
import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;


public class SimpleEntityManager implements EntityManager {
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final SimplePersistenceContext persistenceContext;

    public SimpleEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.persistenceContext = new SimplePersistenceContext();
        this.entityLoader = new EntityLoader(jdbcTemplate, dialect);
        this.entityPersister = new EntityPersister(jdbcTemplate, dialect);
    }

    @Override
    public <T> T persist(T entity) {
        final T persistEntity = entityPersister.insert(entity);

        this.persistenceContext.addEntity(EntityKey.of(entity), persistEntity);

        return persistEntity;
    }

    @Override
    public void remove(Object entity) {
        final EntityMeta entityMeta = EntityMeta.from(entity.getClass());
        final EntityKey entityKey = EntityKey.of(entity.getClass(), entityMeta.getPkValue(entity));

        if (persistenceContext.getEntity(entityKey) != null) {
            persistenceContext.removeEntity(entity);
        }

        entityPersister.delete(entity);
    }

    @Override
    public <T, ID> T find(Class<T> clazz, ID id) {
        EntityKey entityKey = EntityKey.of(clazz, id);

        if (persistenceContext.getEntity(entityKey) != null) {
            return (T) persistenceContext.getEntity(entityKey);
        }

        final T entity = entityLoader.find(clazz, id);
        if (entity != null) {
            persistenceContext.addEntity(entityKey, entity);
        }

        return entity;
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        final List<T> findList = entityLoader.findAll(tClass);
        findList.forEach((it) ->
                persistenceContext.addEntity(EntityKey.of(it), it)
        );
        return findList;
    }


    @Override
    public void flush() {
        final List<Object> changedEntity = persistenceContext.getChangedEntity();

        changedEntity.forEach(entityPersister::update);

        persistenceContext.clear();
    }
}
