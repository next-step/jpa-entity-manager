package jpa;

import persistence.sql.model.EntityId;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityInfo<?> entityInfo = new EntityInfo<>(clazz, id);
        if (persistenceContext.contain(entityInfo)) {
            return clazz.cast(persistenceContext.get(entityInfo));
        }

        T entity = entityLoader.find(clazz, id);
        persistenceContext.add(new EntityInfo<>(entity.getClass(), id), entity);
        return entity;
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
        addPersistenceContext(entity);
    }

    @Override
    public void update(Object entity) {
        entityPersister.update(entity);
        addPersistenceContext(entity);
    }

    private void addPersistenceContext(Object entity) {
        EntityId entityId = new EntityId(entity.getClass());
        String idValue = entityId.getIdValue(entity);
        persistenceContext.add(new EntityInfo<>(entity.getClass(), idValue), entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        removePersistenceContext(entity);
    }

    private void removePersistenceContext(Object entity) {
        EntityId entityId = new EntityId(entity.getClass());
        String idValue = entityId.getIdValue(entity);
        persistenceContext.remove(new EntityInfo<>(entity.getClass(), idValue));
    }

}
