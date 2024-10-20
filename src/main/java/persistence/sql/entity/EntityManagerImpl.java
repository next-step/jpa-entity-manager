package persistence.sql.entity;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(EntityPersister entityPersister, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        if (persistenceContext.containsEntity(clazz, id)) {
            return persistenceContext.getEntity(clazz, id);
        }

        T entity = entityPersister.select(clazz, id);
        if (entity != null) {
            persistenceContext.addEntity(entity, id);
        }
        return entity;
    }

    @Override
    public Object persist(Object entity) {
        Long idValue = entityPersister.getIdValue(entity);
        if (idValue == null) {
            entityPersister.insert(entity);
            idValue = entityPersister.getIdValue(entity);
        }
        entityPersister.update(entity);
        persistenceContext.addEntity(entity, idValue);

        return entity;
    }

    @Override
    public void remove(Object entity) {
        Long idValue = entityPersister.getIdValue(entity);
        if (idValue == null) {
            return;
        }

        persistenceContext.removeEntity(entity.getClass(), idValue);
        entityPersister.delete(entity);
    }

    @Override
    public Object update(Object entity) {
        Long idValue = entityPersister.getIdValue(entity);
        if (persistenceContext.containsEntity(entity.getClass(), idValue)) {
            entityPersister.update(entity);
            persistenceContext.addEntity(entity.getClass(), idValue);
        }
        return entity;
    }

}
