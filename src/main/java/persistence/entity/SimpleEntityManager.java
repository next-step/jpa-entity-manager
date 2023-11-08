package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.metadata.Column;
import persistence.sql.metadata.Columns;
import persistence.sql.metadata.EntityMetadata;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

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

        if(Objects.isNull(entity)) {
            entity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(id, entity);
        }

        return clazz.cast(entity);
    }

    @Override
    public void persist(Object entity) {
        Object id = entityPersister.insert(new EntityMetadata(entity));

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
        EntityMetadata entityMetadata = new EntityMetadata(entity);
        persistenceContext.removeEntity(entityMetadata.getId(), entity);
        entityPersister.delete(entityMetadata);
    }

    @Override
    public void merge(Object entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity);
        Snapshot snapshot = persistenceContext.getCachedDatabaseSnapshot(entityMetadata.getId(), entity);

        if(snapshot == null) {
            persist(entity);
            return;
        }

        Field[] fields = snapshot.getChangedColumns(entity);

        if(fields.length == 0) {
            return;
        }

        persistenceContext.addEntity(entityMetadata.getId(), entity);
        entityPersister.update(
                new Columns(Arrays.stream(fields).map(x -> {
                    try {
                        return new Column(x, String.valueOf(x.get(entity)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList())),
                new EntityMetadata(entity)
        );
    }
}
