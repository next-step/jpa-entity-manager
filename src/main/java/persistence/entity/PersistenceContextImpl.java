package persistence.entity;

import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final HashMap<String, Object> firstLevelCacheMap;

    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<Class<?>, EntityLoader> entityLoaders;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        firstLevelCacheMap = new HashMap<>();

        this.jdbcTemplate = jdbcTemplate;
        this.entityPersisters = new HashMap<>();
        this.entityLoaders = new HashMap<>();
    }

    @Override
    public Object getEntity(Class<?> entityClass, Long id) {
        String cacheKey = getCacheKey(entityClass, id);
        if (firstLevelCacheMap.containsKey(cacheKey)) {
            return firstLevelCacheMap.get(cacheKey);
        }

        EntityLoader entityLoader = getEntityLoader(entityClass);
        Object entity = entityLoader.load(id);
        if (entity != null) {
            doAddEntity(entity);
        }
        return entity;
    }

    @Override
    public void addEntity(Object entity) {
        Long id = getRowId(entity);
        if (id == null) {
            insertEntity(entity);
        } else {
            updateEntity(id, entity);
        }
    }

    private void insertEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        EntityPersister entityPersister = getEntityPersister(entityClass);
        EntityLoader entityLoader = getEntityLoader(entityClass);

        entityPersister.insert(entity);

        Long savedEntityId = entityLoader.getLastId();
        doAddEntity(entityLoader.load(savedEntityId));
    }

    private void updateEntity(Long id, Object entity) {
        EntitySnapshotDifference difference = buildEntitySnapshotDifference(id, entity);
        if (difference.isDirty()) {
            Class<?> entityClass = entity.getClass();
            EntityPersister entityPersister = getEntityPersister(entityClass);

            entityPersister.update(id, difference);

            doAddEntity(entity);
        }
    }

    private EntitySnapshotDifference buildEntitySnapshotDifference(Long id, Object entity) {
        Object oldEntity = firstLevelCacheMap.get(getCacheKey(entity.getClass(), id));
        EntitySnapshot oldEntitySnapshot = new EntitySnapshot(oldEntity);
        EntitySnapshot newEntitySnapshot = new EntitySnapshot(entity);

        return new EntitySnapshotDifference(oldEntitySnapshot, newEntitySnapshot);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityPersister entityPersister = getEntityPersister(entity.getClass());

        Long id = getRowId(entity);
        entityPersister.delete(id);

        String cacheKey = getCacheKey(entity.getClass(), id);
        firstLevelCacheMap.remove(cacheKey);
    }

    private void doAddEntity(Object entity) {
        String cacheKey = getCacheKey(entity.getClass(), getRowId(entity));

        firstLevelCacheMap.put(cacheKey, entity);
    }

    private String getCacheKey(Class<?> entityClass, Long id) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }
        return String.format("%s:%d", entityClass.getName(), id);
    }

    private EntityPersister getEntityPersister(Class<?> entityClass) {
        if (!entityPersisters.containsKey(entityClass)) {
            entityPersisters.put(entityClass, new EntityPersister(jdbcTemplate, entityClass));
        }
        return entityPersisters.get(entityClass);
    }

    private EntityLoader getEntityLoader(Class<?> entityClass) {
        if (!entityLoaders.containsKey(entityClass)) {
            entityLoaders.put(entityClass, new EntityLoader(jdbcTemplate, entityClass));
        }
        return entityLoaders.get(entityClass);
    }

    private static Long getRowId(Object entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());
        return entityMetadata.getPrimaryKeyValue(entity);
    }
}
