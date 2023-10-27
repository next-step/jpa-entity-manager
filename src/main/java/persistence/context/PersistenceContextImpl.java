package persistence.context;

import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.persister.EntityPersister;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {
    private final EntityPersister entityPersister;
    private final Map<Class<?>, Map<String, Object>> FIRST_CACHE = new HashMap<>();
    private final Map<Class<?>, Map<String, Object>> SNAP_SHOT = new HashMap<>();

    public PersistenceContextImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> T getEntity(Class<T> clazz, String id) {
        Object retrieved = Optional.ofNullable(FIRST_CACHE.get(clazz))
                .map(it -> it.get(id))
                .orElse(null);
        if (retrieved == null) {
            Object loaded = entityPersister.load(clazz, id);

            Map<String, Object> entityMap = getOrCreateEntityMap(clazz, SNAP_SHOT);

            entityMap.put(id, createDeepCopy(loaded));

            SNAP_SHOT.put(clazz, entityMap);

            return clazz.cast(loaded);
        }
        return clazz.cast(retrieved);
    }

    @Override
    public <T> T addEntity(T instance, IdAttribute idAttribute) {
        try {
            String id = Optional.ofNullable(idAttribute.getField().get(instance)).map(String::valueOf).orElse(null);

            T snapshot = null;

            if (id == null) {
                if (idAttribute.getGenerationType() == null) {
                    throw new RuntimeException("id가 null이고 generationType이 null 입니다.");
                }

                return entityPersister.insert(instance);
            }

            if (idAttribute.getGenerationType() != null) {
                snapshot = getDatabaseSnapshot(String.valueOf(idAttribute.getField().get(instance)), instance);
            }

            assert snapshot != null;

            return entityPersister.update(snapshot, instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> void removeEntity(T instance, Field idField) {
        Class<?> clazz = instance.getClass();
        Map<String, Object> entityMap = FIRST_CACHE.get(clazz);

        String instanceId = getInstanceId(instance, idField);

        entityMap.remove(instanceId);

        if (entityMap.isEmpty()) {
            FIRST_CACHE.remove(clazz);
        }

        entityPersister.remove(instance, instanceId);
    }

    @Override
    public <T> T getDatabaseSnapshot(String id, T entity) {
        Map<String, Object> entityMap = getOrCreateEntityMap(entity.getClass(), SNAP_SHOT);

        Object snapshot = entityMap.get(id);

        if (snapshot == null) {
            Object loaded = entityPersister.load(entity.getClass(), id);

            snapshot = createDeepCopy(loaded);
            entityMap.put(id, snapshot);
        }
        return (T) snapshot;
    }

    private <T> Map<String, Object> getOrCreateEntityMap(Class<T> clazz, Map<Class<?>, Map<String, Object>> map) {
        return map.computeIfAbsent(clazz, k -> new HashMap<>());
    }

    private <T> String getInstanceId(T instance, Field idField) {
        idField.setAccessible(true);

        try {
            Object idValue = idField.get(instance);

            assert idValue != null;

            return String.valueOf(idValue);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T createDeepCopy(T original) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(original);
            oos.flush();
            ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bin);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("딥카피 실패", e);
        }
    }
}
