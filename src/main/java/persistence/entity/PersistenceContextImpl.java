package persistence.entity;

import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static persistence.entity.Status.*;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<String, Object> firstLevelCacheMap;
    private final EntityEntries entityEntries;

    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<Class<?>, EntityLoader> entityLoaders;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        firstLevelCacheMap = new HashMap<>();
        entityEntries = new EntityEntries();

        this.jdbcTemplate = jdbcTemplate;
        this.entityPersisters = new HashMap<>();
        this.entityLoaders = new HashMap<>();
    }


    @Override
    public Object getEntity(Class<?> entityClass, Long id) {
        return syncEntity(entityClass, id);
    }

    private Object syncEntity(Class<?> entityClass, Long id) {
        String cacheKey = getCacheKey(entityClass, id);

        Status status = entityEntries.getStatus(cacheKey);
        if (status == null) {
            return loadObjectFromEntityLoader(entityClass, id);
        }
        switch (status) {
            case MANAGED:
            case READ_ONLY:
                return firstLevelCacheMap.get(cacheKey);
            case DELETED:
            case GONE:
                throw new ObjectNotFoundException(entityClass, id);
            case LOADING:
            case SAVING:
            default:
                throw new UnsupportedOperationException(
                        String.format("status: %s, entityClass: %s, id: %d", status, entityClass, id));
        }
    }

    private Object loadObjectFromEntityLoader(Class<?> entityClass, Long id) {
        String cacheKey = getCacheKey(entityClass, id);
        entityEntries.setStatus(cacheKey, LOADING);

        EntityLoader entityLoader = entityLoaderOf(entityClass);
        Object entity = entityLoader.load(id);
        if (entity == null) {
            entityEntries.removeStatus(cacheKey);
            return null;
        }
        firstLevelCacheMap.put(cacheKey, entity);
        entityEntries.setStatus(cacheKey, MANAGED);
        return entity;
    }

    @Override
    public void addEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        Long id = getRowId(entity);

        if (id == null) {
            // insert
//            Object createdEntity = insertEntityAndLoadFromDatabase(entity);
            entityPersisterOf(entityClass).insert(entity);

            Long savedEntityId = entityLoaderOf(entityClass).getLastId();
            Object createdEntity = entityLoaderOf(entityClass).load(savedEntityId);

            // set to 1st level cache
            String cacheKey = getCacheKey(createdEntity.getClass(), getRowId(createdEntity));
            firstLevelCacheMap.put(cacheKey, createdEntity);
            entityEntries.setStatus(cacheKey, MANAGED);
        } else {
            String cacheKey = getCacheKey(entityClass, id);

            // 미리 DB와 persistence context 를 싱크
            syncEntity(entityClass, id);
            if (entityEntries.getStatus(cacheKey) != MANAGED) {
                throw new UnsupportedOperationException("updating unmanaged entity");
            }

            entityEntries.setStatus(cacheKey, SAVING);
            Object currentEntity = firstLevelCacheMap.get(cacheKey);
            EntitySnapshotDifference difference = getEntitySnapshotDifference(currentEntity, entity);
            if (difference.isDirty()) {
                entityPersisterOf(entityClass).update(id, difference);
            }
            firstLevelCacheMap.put(cacheKey, entity);
            entityEntries.setStatus(cacheKey, MANAGED);
        }
    }

    private static EntitySnapshotDifference getEntitySnapshotDifference(Object oldEntity, Object newEntity) {
        EntitySnapshot oldEntitySnapshot = new EntitySnapshot(oldEntity);
        EntitySnapshot newEntitySnapshot = new EntitySnapshot(newEntity);

        return new EntitySnapshotDifference(oldEntitySnapshot, newEntitySnapshot);
    }

    @Override
    public void removeEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        Long id = getRowId(entity);
        String cacheKey = getCacheKey(entityClass, id);

        // 미리 DB와 persistence context 를 싱크
        syncEntity(entityClass, id);

        entityEntries.setStatus(cacheKey, DELETED);
        entityPersisterOf(entityClass).delete(id);
        firstLevelCacheMap.remove(cacheKey);
        entityEntries.setStatus(cacheKey, GONE);
    }

    private static String getCacheKey(Class<?> entityClass, Long id) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }
        return String.format("%s:%d", entityClass.getName(), id);
    }

    private EntityPersister entityPersisterOf(Class<?> entityClass) {
        if (!entityPersisters.containsKey(entityClass)) {
            entityPersisters.put(entityClass, new EntityPersister(jdbcTemplate, entityClass));
        }
        return entityPersisters.get(entityClass);
    }

    private EntityLoader entityLoaderOf(Class<?> entityClass) {
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
