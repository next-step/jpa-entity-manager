package persistence.entity;

import jdbc.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<?>, EntityPersister> entityPersisterMap = new HashMap<>();

    private EntityManagerImpl(PersistenceContext persistenceContext, JdbcTemplate jdbcTemplate) {
        this.persistenceContext = persistenceContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    public static EntityManager createDefault(JdbcTemplate jdbcTemplate) {
        return new EntityManagerImpl(
                new PersistenceContextImpl(),
                jdbcTemplate
        );
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        if (persistenceContext.contains(entityKey)) {
            return clazz.cast(persistenceContext.get(entityKey));
        }

        EntityPersister entityPersister = getEntityPersister(clazz);
        T entity = entityPersister.find(id);
        persistenceContext.put(entityKey, entity);

        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        EntityPersister entityPersister = getEntityPersister(entity.getClass());
        EntityKey entityKey = entityPersister.insert(entity);
        persistenceContext.put(entityKey, entity);

        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        EntityPersister entityPersister = getEntityPersister(entity.getClass());
        EntityKey entityKey = new EntityKey(entity.getClass(), entityPersister.getIdValue(entity).value());
        persistenceContext.remove(entityKey);
        entityPersister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntityPersister entityPersister = getEntityPersister(entity.getClass());
        EntityKey entityKey = new EntityKey(entity.getClass(), entityPersister.getIdValue(entity).value());
        EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey);
        if (entitySnapshot.isDirty(entity)) {
            entityPersister.update(entity, entitySnapshot);
        }

        persistenceContext.put(entityKey, entity);
        return entity;
    }

    private EntityPersister getEntityPersister(Class<?> clazz) {
        return entityPersisterMap.computeIfAbsent(clazz, k -> EntityPersister.createDefault(jdbcTemplate, clazz));
    }
}
