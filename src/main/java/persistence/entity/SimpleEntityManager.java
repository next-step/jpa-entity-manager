package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.context.EntitySnapshot;
import persistence.context.PersistenceContext;
import persistence.context.SimplePersistenceContext;
import persistence.sql.dialect.Dialect;
import persistence.sql.metadata.EntityMetadata;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext = new SimplePersistenceContext();

    public SimpleEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityPersister = new EntityPersister(jdbcTemplate, dialect);
        this.entityLoader = new EntityLoader(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = persistenceContext.getEntity(clazz, id);

        if (Objects.nonNull(entity)) {
            return entity;
        }

        Object object = entityLoader.find(EntityId.of(clazz, id));
        persistenceContext.addEntity(id, object);

        return clazz.cast(object);
    }

    @Override
    public Object persist(Object entity) {
        Object id = entityPersister.insert(entity);

        cachedEntity(id, entity);
        return entity;
    }

    private void cachedEntity(Object id, Object entity) {
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass(), entity);
        Object key = entityMetadata.getPrimaryKey().getValue();

        EntitySnapshot snapshot = persistenceContext.getDatabaseSnapshot(key, entity);
        if (snapshot.isNotEqualToSnapshot(entity)) {
            entityPersister.update(entity);
            cachedEntity(key, entity);
        }

        return entity;
    }
}
