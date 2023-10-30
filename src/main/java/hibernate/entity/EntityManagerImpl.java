package hibernate.entity;

import hibernate.EntityEntry;
import hibernate.entity.meta.EntityClass;
import hibernate.entity.meta.column.EntityColumn;
import hibernate.entity.persistencecontext.EntityKey;
import hibernate.entity.persistencecontext.EntitySnapshot;
import hibernate.entity.persistencecontext.PersistenceContext;

import java.util.Map;

import static hibernate.Status.*;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;
    private final EntityEntryContext entityEntryContext;

    public EntityManagerImpl(
            final EntityPersister entityPersister,
            final EntityLoader entityLoader,
            final PersistenceContext persistenceContext,
            final EntityEntryContext entityEntryContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
        this.entityEntryContext = entityEntryContext;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        Object persistenceContextEntity = persistenceContext.getEntity(entityKey);
        if (persistenceContextEntity != null) {
            entityEntryContext.getEntityEntry(persistenceContextEntity)
                    .updateStatus(MANAGED);
            return (T) persistenceContextEntity;
        }

        EntityEntry entityEntry = new EntityEntry(SAVING);
        EntityClass<T> entityClass = EntityClass.getInstance(clazz);
        T loadEntity = entityLoader.find(entityClass, id);
        persistNewEntity(loadEntity, id);
        entityEntry.updateStatus(MANAGED);
        return loadEntity;
    }

    private void persistNewEntity(final Object entity, final Object entityId) {
        persistenceContext.addEntity(entityId, entity);
        persistenceContext.getDatabaseSnapshot(entityId, entity);
    }

    @Override
    public void persist(final Object entity) {
        EntityColumn entityId = EntityClass.getInstance(entity.getClass())
                .getEntityId();
        Object id = entityId.getFieldValue(entity);
        if (id == null) {
            entityEntryContext.addEntityEntry(entity, new EntityEntry(SAVING));
            Object generatedId = entityPersister.insert(entity);
            entityId.assignFieldValue(entity, generatedId);
            persistNewEntity(entity, generatedId);
            entityEntryContext.getEntityEntry(entity)
                    .updateStatus(MANAGED);
            return;
        }

        if (persistenceContext.getEntity(new EntityKey(id, entity)) != null) {
            throw new IllegalStateException("이미 영속화되어있는 entity입니다.");
        }
        persistNewEntity(entity, id);
        entityEntryContext.addEntityEntry(entity, new EntityEntry(SAVING));
        entityPersister.insert(entity);
        entityEntryContext.getEntityEntry(entity)
                .updateStatus(MANAGED);
    }

    @Override
    public void merge(final Object entity) {
        EntityClass<?> entityClass = EntityClass.getInstance(entity.getClass());
        Object entityId = getNotNullEntityId(entityClass, entity);
        Map<EntityColumn, Object> changedColumns = getSnapshot(entity, entityId).changedColumns(entity);
        if (changedColumns.isEmpty()) {
            return;
        }
        entityPersister.update(entityClass, entityId, changedColumns);
        persistNewEntity(entity, entityId);
    }

    private Object getNotNullEntityId(final EntityClass<?> entityClass, final Object entity) {
        Object entityId = entityClass.getEntityId()
                .getFieldValue(entity);
        if (entityId == null) {
            throw new IllegalStateException("id가 없는 entity는 merge할 수 없습니다.");
        }
        return entityId;
    }

    private EntitySnapshot getSnapshot(final Object entity, final Object entityId) {
        EntityKey entityKey = new EntityKey(entityId, entity.getClass());
        EntitySnapshot snapshot = persistenceContext.getCachedDatabaseSnapshot(entityKey);
        if (snapshot == null) {
            find(entity.getClass(), entityId);
            return persistenceContext.getCachedDatabaseSnapshot(entityKey);
        }
        return snapshot;
    }

    @Override
    public void remove(final Object entity) {
        persistenceContext.removeEntity(entity);
        Object id = EntityClass.getInstance(entity.getClass())
                .getEntityId()
                .getFieldValue(entity);
        EntityEntry entityEntry = new EntityEntry(DELETED);
        entityPersister.delete(entity);
        entityEntry.updateStatus(GONE);
    }
}
