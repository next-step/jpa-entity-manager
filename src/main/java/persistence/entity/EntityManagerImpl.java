package persistence.entity;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityManagerImpl implements EntityManager {
    private final transient StatefulPersistenceContext persistenceContext;
    private final QueryBuilder queryBuilder;

    private EntityManagerImpl(StatefulPersistenceContext persistenceContext, QueryBuilder queryBuilder) {
        this.persistenceContext = persistenceContext;
        this.queryBuilder = queryBuilder;
    }

    public EntityManagerImpl(QueryBuilder queryBuilder) {
        this(initStatefulPersistenceContext(), queryBuilder);
    }

    private static StatefulPersistenceContext initStatefulPersistenceContext() {
        return new StatefulPersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Long key) {
        EntityKey entityKey = EntityKey.of(key, clazz.getSimpleName());
        if (persistenceContext.containsEntity(entityKey)) {
            return clazz.cast(persistenceContext.getEntity(entityKey));
        }

        Object object = queryBuilder.findById(clazz, key);
        persistenceContext.addEntity(entityKey, object);

        return clazz.cast(persistenceContext.getEntity(entityKey));
    }

    @Override
    public void persist(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);
        EntityKey entityKey = EntityKey.of((Long) key, clazz.getSimpleName());

        Object cacheEntity = persistenceContext.getCachedDatabaseSnapshot(entityKey);

        if (cacheEntity.equals(entity)) {
            return;
        }

        queryBuilder.save(entity);
        persistenceContext.addEntity(entityKey, entity);
    }

    @Override
    public void remove(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);
        EntityKey entityKey = EntityKey.of((Long) key, clazz.getSimpleName());

        queryBuilder.delete(clazz, (Long) key);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public boolean contains(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);

        return persistenceContext.containsEntity(EntityKey.of((Long) key, clazz.getSimpleName()));
    }

    private Field unique(Field[] field) {
        return Arrays.stream(field)
                .filter(it -> it.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Object getKey(Object entity) throws IllegalAccessException {
        Field keyField = unique(entity.getClass().getDeclaredFields());
        keyField.setAccessible(true);
        return keyField.get(entity);
    }
}
