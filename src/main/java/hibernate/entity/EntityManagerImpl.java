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

    public EntityManagerImpl(
            final EntityPersister entityPersister,
            final EntityLoader entityLoader,
            final PersistenceContext persistenceContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        Object persistenceContextEntity = persistenceContext.getEntity(entityKey);
        if (persistenceContextEntity != null) {
            return (T) persistenceContextEntity;
        }

        EntityEntry entityEntry = new EntityEntry(LOADING);
        EntityClass<T> entityClass = EntityClass.getInstance(clazz);
        T loadEntity = entityLoader.find(entityClass, id);
        persistenceContext.addEntity(id, loadEntity);
        return loadEntity;
    }

    @Override
    public void persist(final Object entity) {
        EntityColumn entityId = EntityClass.getInstance(entity.getClass())
                .getEntityId();
        Object id = entityId.getFieldValue(entity);
        if (id == null) {
            persistenceContext.addEntityEntry(entity, SAVING);
            Object generatedId = entityPersister.insert(entity);
            entityId.assignFieldValue(entity, generatedId);
            persistenceContext.addEntity(generatedId, entity);
            return;
        }

        if (persistenceContext.getEntity(new EntityKey(id, entity)) != null) {
            throw new IllegalStateException("이미 영속화되어있는 entity입니다.");
        }
        EntityEntry entityEntry = persistenceContext.addEntity(id, entity, SAVING);
        entityPersister.insert(entity);
        entityEntry.updateStatus(MANAGED);
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
        persistenceContext.addEntity(entityId, entity);
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
        EntitySnapshot snapshot = persistenceContext.getDatabaseSnapshot(entityKey);
        if (snapshot == null) {
            find(entity.getClass(), entityId);
            return persistenceContext.getDatabaseSnapshot(entityKey);
        }
        return snapshot;
    }

    @Override
    public void remove(final Object entity) {
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }
}
