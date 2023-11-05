package persistence.entity;

import jdbc.JdbcTemplate;
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
        T entity = findPersistedEntity(clazz, id)
                .orElse(loadEntity(clazz, id));

        persistEntity(clazz, id, entity);

        return entity;
    }


    private <T> T findByEntity(T entity) {
        return (T) find(entity.getClass(), (Long) getEntityId(entity));
    }

    @Override
    public void persist(Object entity) {
        EntityPersister entityPersister = entityManagementCache.persister(entity.getClass());
        Object persistedEntity = findByEntity(entity);

        if (persistedEntity != null) {
            entityPersister.update(entity);
            entity = persistedEntity;
        } else {
            entityPersister.insert(entity);
        }

        persistEntity((Class<Object>) entity.getClass(), (Long) getEntityId(entity), entity);
    }

    @Override
    public void remove(Object entity) {
        EntityPersister entityPersister = entityManagementCache.persister(entity.getClass());

        entityPersister.delete(entity);
        removeEntity(entity);
    }

    private <T> T loadEntity(Class<T> clazz, Long id) {
        return entityManagementCache.loader(clazz).findById(clazz, id);
    }

    private <T> void persistEntity(Class<T> clazz, Long id, T entity) {
        PersistenceContext<T> persistenceContext = entityManagementCache.persistenceContext(clazz);
        persistenceContext.addEntity(new EntityPersistIdentity(clazz, id), entity);
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
}
