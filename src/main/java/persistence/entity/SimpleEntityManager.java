package persistence.entity;

import persistence.sql.metadata.EntityMetadata;

import java.lang.reflect.Field;

public class SimpleEntityManager implements EntityManager{
    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(EntityPersister entityPersister, EntityLoader entityLoader, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        Object entity = persistenceContext.getEntity(clazz, id);

        if(entity == null) {
            entity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(id, entity);
        }

        return clazz.cast(entity);
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.addEntity(entityPersister.insert(entity), entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

    @Override
    public void merge(Object entity) {
        Object id = new EntityMetadata(entity).getId();
        Snapshot snapshot = persistenceContext.getCachedDatabaseSnapshot(id, entity);

        if(snapshot == null) {
            persist(entity);
            return;
        }

        Field[] fields = snapshot.getChangedColumns(entity);

        if(fields.length == 0) {
            return;
        }

        persistenceContext.addEntity(id, entity);
        entityPersister.update(fields, entity);
    }
}
