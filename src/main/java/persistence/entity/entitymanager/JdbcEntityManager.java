package persistence.entity.entitymanager;

import jdbc.JdbcTemplate;
import persistence.entity.persistencecontext.EntityEntry;
import persistence.entity.persistencecontext.EntityPersistIdentity;
import persistence.entity.persistencecontext.EntitySnapShot;
import persistence.entity.persistencecontext.EntityStatus;
import persistence.entity.persistencecontext.PersistenceContext;
import persistence.sql.dbms.Dialect;
import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityTable;

import java.util.Optional;

public class JdbcEntityManager implements EntityManager {
    private final EntityManagementCache entityManagementCache;

    public JdbcEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityManagementCache = new EntityManagementCache(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        if (id == null) {
            return null;
        }

        T entity = findPersistedEntity(clazz, id)
                .orElse(loadEntity(clazz, id));

        if (entity == null) {
            return null;
        }

        persistEntity(clazz, id, entity);
        updateEntityStatus(id, entity, EntityStatus.LOADING);

        return entity;
    }

    private <T> T findByEntity(T entity) {
        return (T) find(entity.getClass(), (Long) getEntityId(entity));
    }

    @Override
    public void persist(Object entity) {
        EntityPersister entityPersister = entityManagementCache.persister(entity.getClass());

        Object generetedId = entityPersister.insert(entity);
        EntityTable entityTable = new EntityTable<>(entity.getClass());
        EntityColumn idColumn = entityTable.getIdColumn();
        idColumn.setValue(entity, generetedId);

        persistEntity((Class<Object>) entity.getClass(), (Long) getEntityId(entity), entity);
    }

    @Override
    public void merge(Object entity) {
        PersistenceContext persistenceContext = entityManagementCache.persistenceContext(entity.getClass());

        EntitySnapShot entitySnapShot = persistenceContext.getDatabaseSnapshot(new EntityPersistIdentity(entity.getClass(), getEntityId(entity)), entity);

        if (isDirty(entity, entitySnapShot)) {
            EntityPersister entityPersister = entityManagementCache.persister(entity.getClass());
            entityPersister.update(entity);
            persistEntity((Class<Object>) entity.getClass(), (Long) getEntityId(entity), entity);
        }
    }

    private boolean isDirty(Object entity, EntitySnapShot snapShot) {
        return !snapShot.getDirtyColumns(entity).isEmpty();
    }

    @Override
    public void remove(Object entity) {
        EntityPersister entityPersister = entityManagementCache.persister(entity.getClass());
        PersistenceContext persistenceContext = entityManagementCache.persistenceContext(entity.getClass());
        EntityEntry entityEntry = persistenceContext.getEntityEntry(new EntityPersistIdentity(entity.getClass(), getEntityId(entity)));

        updateEntityStatus((Long) getEntityId(entity), entity, EntityStatus.DELETED);
        entityPersister.delete(entity);
        removeEntity(entity);
        updateEntityStatus((Long) getEntityId(entity), entity, EntityStatus.GONE);
    }

    private <T> T loadEntity(Class<T> clazz, Long id) {
        return entityManagementCache.loader(clazz).findById(clazz, id);
    }

    private <T> void persistEntity(Class<T> clazz, Long id, T entity) {
        PersistenceContext<T> persistenceContext = entityManagementCache.persistenceContext(clazz);
        EntityPersistIdentity persistId = new EntityPersistIdentity(clazz, id);
        persistenceContext.addEntity(persistId, entity);
        persistenceContext.getDatabaseSnapshot(persistId, entity);
    }

    private <T> void removeEntity(T entity) {
        PersistenceContext<T> persistenceContext = (PersistenceContext<T>) entityManagementCache.persistenceContext(entity.getClass());
        persistenceContext.removeEntity(entity);
    }

    private <T> Optional<T> findPersistedEntity(Class<T> clazz, Object id) {
        PersistenceContext<T> persistenceContext = entityManagementCache.persistenceContext(clazz);

        return Optional.ofNullable(persistenceContext.getEntity(new EntityPersistIdentity(clazz, id)));
    }

    private <T> Object getEntityId(T entity) {
        Class<T> entityClass = (Class<T>) entity.getClass();
        EntityTable<T> entityTable = new EntityTable<>(entityClass);
        EntityColumn<T, ?> idColumn = entityTable.getIdColumn();
        Object entityId = idColumn.getValue(entity);

        return entityId;
    }

    private <T> void updateEntityStatus(Long id, T entity, EntityStatus status) {
        PersistenceContext persistenceContext = entityManagementCache.persistenceContext(entity.getClass());
        persistenceContext.getEntityEntry(new EntityPersistIdentity(entity.getClass(), id))
                .updateStatus(status);
    }
}
