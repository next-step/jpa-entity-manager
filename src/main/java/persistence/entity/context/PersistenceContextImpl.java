package persistence.entity.context;

import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;
import persistence.entity.data.EntitySnapshot;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static persistence.entity.context.Status.*;

public class PersistenceContextImpl implements PersistenceContext {
    private final FirstLevelCache firstLevelCache;
    private final EntityEntries entityEntries;

    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<Class<?>, EntityLoader> entityLoaders;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        firstLevelCache = new FirstLevelCache();
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
        Status status = entityEntries.getStatus(entityClass, id);
        // XXX: 코드정리
        if (status == null) {
            return getObject(entityClass, id);
        }

        return getObject(entityClass, id, status);
    }

    private Object getObject(Class<?> entityClass, Long id, Status status) {
        switch (status) {
            case MANAGED:
            case READ_ONLY:
                return firstLevelCache.get(entityClass, id);
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

    private Object getObject(Class<?> entityClass, Long id) {
        entityEntries.setStatus(entityClass, id, LOADING);

        Optional<Object> fetched = entityLoaderOf(entityClass).load(id);
        if (fetched.isEmpty()) {
            entityEntries.removeStatus(entityClass, id);
            return null;
        }
        Object entity = fetched.get();
        firstLevelCache.store(entityClass, id, entity);
        entityEntries.setStatus(entityClass, id, MANAGED);
        return entity;
    }

    @Override
    public void addEntity(Object newEntity) {
        if (isInsertOperation(newEntity)) {
            insertNewEntity(newEntity);
        } else {
            updateExistingEntity(newEntity);
        }
    }

    /**
     * entity 에 id=null 이거나, id 값으로 database 에서 조회가 안되면 insert 해야 한다고 판단한다.
     */
    private boolean isInsertOperation(Object newEntity) {
        Long id = getRowId(newEntity);
        if (id == null) return true;

        return getEntity(newEntity.getClass(), id) == null;
    }

    private void insertNewEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        Long id = getRowId(entity);

        entityPersisterOf(entityClass).insert(entity);

        Long savedEntityId = entityLoaderOf(entityClass).getLastId();
        Optional<Object> load = entityLoaderOf(entityClass).load(savedEntityId);
        Object createdEntity = load.get();

        // set to 1st level cache
        firstLevelCache.store(entityClass, savedEntityId, createdEntity);
        entityEntries.setStatus(createdEntity.getClass(), getRowId(createdEntity), MANAGED);
    }

    private void updateExistingEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        Long id = getRowId(entity);

        syncEntity(entityClass, id);

        if (entityEntries.getStatus(entityClass, id) != MANAGED) {
            throw new UnsupportedOperationException("updating unmanaged entity");
        }

        entityEntries.setStatus(entityClass, id, SAVING);
        Object currentEntity = firstLevelCache.get(entityClass, id);
        Map<String, Object> changes = getEntitySnapshotChanges(currentEntity, entity);
        if (!changes.isEmpty()) {
            entityPersisterOf(entityClass).update(id, changes);
        }
        firstLevelCache.store(entityClass, id, entity);
        entityEntries.setStatus(entityClass, id, MANAGED);
    }

    private static Map<String, Object> getEntitySnapshotChanges(Object oldEntity, Object newEntity) {
        EntitySnapshot oldEntitySnapshot = EntitySnapshot.of(oldEntity);
        EntitySnapshot newEntitySnapshot = EntitySnapshot.of(newEntity);

        return oldEntitySnapshot.changes(newEntitySnapshot);
    }

    @Override
    public void removeEntity(Object entity) {
        Class<?> entityClass = entity.getClass();
        Long id = getRowId(entity);

        syncEntity(entityClass, id);

        entityEntries.setStatus(entityClass, id, DELETED);
        firstLevelCache.delete(entityClass, id);
        entityPersisterOf(entityClass).delete(id);
        entityEntries.setStatus(entityClass, id, GONE);
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

// XXX: sync 말고 다른 액션은 status 에 따라 행동할 거 없나?
// insert
// update
// remove
