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
        persistenceContext.addEntity(new EntityKey(selected), selected);
        persistenceContext.getDatabaseSnapshot(new EntityKey(selected), selected);
        return selected;
    }

    @Override
    public void persist(Object entity) {
        Class<?> entityClass = entity.getClass();
        EntityData entityData = new EntityData(entityClass);

        Object idValue = ReflectionUtil.getValueFrom(entityData.getEntityColumns().getIdColumn().getField(), entity);
        if (idValue == null) {
            entityPersister.insert(entity);
            return;
        }

        Object foundEntity = find(entityClass, idValue);
        if (foundEntity == null) {
            entityPersister.insert(entity);
        } else {
            entityPersister.update(entity);
        }
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

}
