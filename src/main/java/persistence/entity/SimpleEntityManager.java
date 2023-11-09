package persistence.entity;

import persistence.sql.entity.EntityData;
import util.ReflectionUtil;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(
            EntityPersister entityPersister,
            EntityLoader entityLoader,
            PersistenceContext persistenceContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T, K> T find(Class<T> clazz, K id) {
        Object cached = persistenceContext.getEntity(new EntityKey(clazz, id));
        if (cached != null) {
            return (T) cached;
        }

        T selected = entityLoader.selectById(clazz, id);
        if (selected == null) {
            return null;
        }

        persistenceContext.addEntity(new EntityKey(selected), selected);
        persistenceContext.getDatabaseSnapshot(new EntityKey(selected), selected);
        persistenceContext.addEntityEntry(selected, new SimpleEntityEntry(selected, Status.MANAGED));
        return selected;
    }

    @Override
    public void persist(Object entity) {
        Class<?> entityClass = entity.getClass();
        EntityData entityData = new EntityData(entityClass);
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);

        if (entityEntry != null && entityEntry.getStatus().equals(Status.READ_ONLY)) {
            throw new IllegalStateException("READ_ONLY는 저장할 수 없습니다.");
        }

        persistenceContext.addEntityEntry(entity, new SimpleEntityEntry(entity, Status.SAVING));

        Object idValue = ReflectionUtil.getValueFrom(entityData.getEntityColumns().getIdColumn().getField(), entity);
        if (idValue == null) {
            entityPersister.insert(entity);
            persistenceContext.addEntity(new EntityKey(entity), entity);
            persistenceContext.updateEntityEntryStatus(entity, Status.MANAGED);
            return;
        }

        Object foundEntity = find(entityClass, idValue);
        if (foundEntity == null) {
            entityPersister.insert(entity);
        } else {
            entityPersister.update(entity);
            persistenceContext.removeEntity(new EntityKey(entity));
        }
        persistenceContext.addEntity(new EntityKey(entity), entity);
        persistenceContext.updateEntityEntryStatus(entity, Status.MANAGED);
    }

    @Override
    public void remove(Object entity) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);

        if (entityEntry != null && entityEntry.getStatus().equals(Status.READ_ONLY)) {
            throw new IllegalStateException("READ_ONLY는 저장할 수 없습니다.");
        }

        if (entityEntry != null) {
            persistenceContext.updateEntityEntryStatus(entity, Status.DELETED);
        }

        entityPersister.delete(entity);

        if (entityEntry != null) {
            persistenceContext.removeEntity(new EntityKey(entity));
            persistenceContext.updateEntityEntryStatus(entity, Status.GONE); // TODO : commit이 구별되어야 함
        }
    }

}
