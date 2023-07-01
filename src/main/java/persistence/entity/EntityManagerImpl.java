package persistence.entity;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;

import static persistence.entity.PersistenceContext.NO_ROW;

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
        persist(entity.getClass(), entity);
    }

    @Override
    public void persist(Class<?> clazz, Object entity) throws IllegalAccessException {
        Object key = getKey(entity);
        EntityKey entityKey = EntityKey.of((Long) key, clazz.getSimpleName());

        queryBuilder.save(entity);
        persistenceContext.addEntity(entityKey, entity);
    }

    @Override
    public void remove(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);
        EntityKey entityKey = getEntityKey(entity);

        queryBuilder.delete(clazz, (Long) key);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public boolean contains(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);

        return persistenceContext.containsEntity(EntityKey.of((Long) key, clazz.getSimpleName()));
    }

    @Override
    public boolean isChanged(Object entity) throws IllegalAccessException {
        EntityKey entityKey = EntityKey.of(getKey(entity), entity.getClass().getSimpleName());
        Object cache = persistenceContext.getCached(entityKey);
        if (cache == NO_ROW) {
            return true;
        }

        Proxy snapshot = persistenceContext.getCachedDatabaseSnapshot(entityKey);
        if (snapshot == null) {
            return true;
        }
        Object snapshotObject = snapshot.entity();
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .anyMatch(field -> {
                    Object fieldValueCache = getFieldValue(field, cache);
                    Object fieldValueSnapshot = getFieldValue(field, snapshotObject);
                    return !fieldValueCache.equals(fieldValueSnapshot);
                });
    }

    @Override
    public void update(Class<?> clazz, Proxy proxy) throws IllegalAccessException {
        Object entity = proxy.entity();
        Object key = getKey(entity);
        EntityKey entityKey = EntityKey.of(getKey(entity), entity.getClass().getSimpleName());

        Proxy changedProxy = proxy.toDirty(persistenceContext.getCachedDatabaseSnapshot(entityKey));
        queryBuilder.update((Long) key, clazz.getSimpleName(), changedProxy.entity());
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

    private String getFieldValue(Field field, Object object) {
        try {
            field.setAccessible(true);
            return String.valueOf(field.get(object));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private EntityKey getEntityKey(Object entity) throws IllegalAccessException {
        Class<?> clazz = entity.getClass();
        Object key = getKey(entity);
        return EntityKey.of((Long) key, clazz.getSimpleName());
    }
}
