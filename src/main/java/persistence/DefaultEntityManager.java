package persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;

public class DefaultEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public DefaultEntityManager(EntityPersister entityPersister, EntityLoader entityLoader, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        validId(id);

        T entity = (T) persistenceContext.getEntity(id);

        if (entity == null) {
            entity = entityLoader.load(clazz, id);
        }

        if (entity != null) {
            persistenceContext.getCachedDatabaseSnapshot(id, entity);
        }

        return entity;
    }

    private static void validId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("[EntityManager] find: id is null");
        }
    }

    @Override
    public <T> T persist(T entity) {
        validEntity(entity);

        Long id = (Long) entityPersister.insert(entity);
        persistenceContext.addEntity(id, entity);
        persistenceContext.getCachedDatabaseSnapshot(id, entity);

        injectIdToEntity(entity, id);
        return entity;
    }

    private static void injectIdToEntity(Object entity, Long id) {
        Field field = Arrays.stream(entity.getClass()
                        .getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("not found entity Id field"));

        field.setAccessible(true);
        try {
            field.set(entity, id);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("not access %s entity %s field", entity.getClass().getTypeName(), field.getName()));
        }
    }

    @Override
    public void remove(Object entity) {
        validEntity(entity);

        entityPersister.delete(entity);
        persistenceContext.removeEntity(new EntityMetadata(entity).getIdValue());
    }

    @Override
    public <T> T merge(Long id, T entity) {
        Object snapshot = persistenceContext.getCachedDatabaseSnapshot(id, entity);

        if (!entity.equals(snapshot)) {
            entityPersister.update(id, entity);
            persistenceContext.addEntity(id, entity);
        }

        return entity;
    }

    private static void validEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("[EntityManager] persist: the instance is not an entity");
        }
    }
}
