package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.metadata.EntityMetadata;

import java.lang.reflect.Field;
import java.util.Arrays;

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
        Object id = entityPersister.insert(entity);

        Field idField = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(x -> x.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Id 값이 정의되지 않은 엔티티입니다."));

        try {
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        persistenceContext.addEntity(id, entity);
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
