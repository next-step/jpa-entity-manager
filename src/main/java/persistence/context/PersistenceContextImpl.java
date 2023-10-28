package persistence.context;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributeHolder;
import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.persister.EntityPersister;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {
    private final EntityAttributeHolder entityAttributeHolder;
    private final EntityPersister entityPersister;
    private final Map<Class<?>, Map<String, Object>> FIRST_CACHE = new HashMap<>();
    private final Map<Class<?>, Map<String, Object>> SNAP_SHOT = new HashMap<>();

    public PersistenceContextImpl(EntityPersister entityPersister, EntityAttributeHolder entityAttributeHolder) {
        this.entityPersister = entityPersister;
        this.entityAttributeHolder = entityAttributeHolder;
    }

    @Override
    public <T> void removeEntity(T instance) {
        EntityAttribute entityAttribute = entityAttributeHolder.findEntityAttribute(instance.getClass());

        Field idField = entityAttribute.getIdAttribute().getField();

        Class<?> clazz = instance.getClass();
        Map<String, Object> firstCacheEntityMap = FIRST_CACHE.get(clazz);
        Map<String, Object> snapShotEntityMap = SNAP_SHOT.get(clazz);

        String instanceId = getInstanceIdAsString(instance, idField);

        firstCacheEntityMap.remove(instanceId);

        if (firstCacheEntityMap.isEmpty()) {
            FIRST_CACHE.remove(clazz);
        }

        if (snapShotEntityMap.isEmpty()) {
            SNAP_SHOT.remove(clazz);
        }

        entityPersister.remove(instance, instanceId);
    }

    @Override
    public <T> T getEntity(Class<T> clazz, String id) {
        Object retrieved = Optional.ofNullable(FIRST_CACHE.get(clazz))
                .map(it -> it.get(id))
                .orElse(null);

        if (retrieved == null) {
            Object loaded = entityPersister.load(clazz, id);

            Map<String, Object> firstCacheEntityMap = getOrCreateEntityMap(clazz, FIRST_CACHE);
            Map<String, Object> snapShotEntityMap = getOrCreateEntityMap(clazz, SNAP_SHOT);

            if (loaded != null) {
                firstCacheEntityMap.put(id, loaded);
                FIRST_CACHE.put(clazz, firstCacheEntityMap);

                snapShotEntityMap.put(id, createDeepCopy(loaded));
                SNAP_SHOT.put(clazz, snapShotEntityMap);
            }

            return clazz.cast(loaded);
        }
        return clazz.cast(retrieved);
    }

    @Override
    public <T> T addEntity(T instance) {
        try {
            EntityAttribute entityAttribute = entityAttributeHolder.findEntityAttribute(instance.getClass());
            IdAttribute idAttribute = entityAttribute.getIdAttribute();

            Map<String, Object> snapShotEntityMap = getOrCreateEntityMap(instance.getClass(), SNAP_SHOT);
            Map<String, Object> firstcacheEntityMap = getOrCreateEntityMap(instance.getClass(), FIRST_CACHE);

            Field idField = idAttribute.getField();
            idField.setAccessible(true);

            String id = getInstanceIdAsString(instance, idField);

            T snapshot = null;

            if (id == null) {
                if (idAttribute.getGenerationType() == null) {
                    throw new RuntimeException("id가 null이고 generationType이 null 입니다.");
                }

                T inserted = entityPersister.insert(instance);

                snapShotEntityMap.put(getInstanceIdAsString(instance, idAttribute.getField()), createDeepCopy(inserted));
                firstcacheEntityMap.put(getInstanceIdAsString(instance, idAttribute.getField()), inserted);

                SNAP_SHOT.put(inserted.getClass(), snapShotEntityMap);
                FIRST_CACHE.put(inserted.getClass(), firstcacheEntityMap);

                return inserted;
            }

            if (idAttribute.getGenerationType() != null) {
                snapshot = getDatabaseSnapshot(instance, String.valueOf(idAttribute.getField().get(instance)));
            }

            assert snapshot != null;

            snapShotEntityMap.put(getInstanceIdAsString(instance, idAttribute.getField()), instance);
            firstcacheEntityMap.put(getInstanceIdAsString(instance, idAttribute.getField()), instance);

            SNAP_SHOT.put(instance.getClass(), snapShotEntityMap);
            FIRST_CACHE.put(instance.getClass(), firstcacheEntityMap);

            return entityPersister.update(snapshot, instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getDatabaseSnapshot(T instance, String id) {
        Map<String, Object> entityMap = getOrCreateEntityMap(instance.getClass(), SNAP_SHOT);

        Object snapshot = entityMap.get(id);

        if (snapshot == null) {
            Object loaded = entityPersister.load(instance.getClass(), id);

            snapshot = createDeepCopy(loaded);
            entityMap.put(id, snapshot);
        }
        return (T) snapshot;
    }

    private <T> Map<String, Object> getOrCreateEntityMap(Class<T> clazz, Map<Class<?>, Map<String, Object>> map) {
        return map.computeIfAbsent(clazz, k -> new HashMap<>());
    }

    private <T> String getInstanceIdAsString(T instance, Field idField) {
        idField.setAccessible(true);

        try {
            return Optional.ofNullable(idField.get(instance)).map(String::valueOf).orElse(null);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T createDeepCopy(T original) {
        try {
            Class<?> clazz = original.getClass();
            T copy = (T) clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(original);
                if (value != null && !isPrimitiveOrWrapper(value.getClass())) {
                    field.set(copy, createDeepCopy(value));
                } else {
                    field.set(copy, value);
                }
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("딥카피 실패", e);
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || (clazz == Double.class) || (clazz == Float.class) || (clazz == Long.class)
                || (clazz == Integer.class) || (clazz == Short.class) || (clazz == Character.class)
                || (clazz == Byte.class) || (clazz == Boolean.class) || (clazz == String.class);
    }
}
