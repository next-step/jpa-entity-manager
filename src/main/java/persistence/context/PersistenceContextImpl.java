package persistence.context;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributeCenter;
import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.persister.SimpleEntityPersister;

import java.lang.reflect.Field;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {
    private final EntityAttributeCenter entityAttributeCenter;
    private final SimpleEntityPersister simpleEntityPersister;
    private final FirstCaches firstCaches = new FirstCaches();
    private final SnapShots snapShots = new SnapShots();

    public PersistenceContextImpl(SimpleEntityPersister simpleEntityPersister, EntityAttributeCenter entityAttributeCenter) {
        this.simpleEntityPersister = simpleEntityPersister;
        this.entityAttributeCenter = entityAttributeCenter;
    }

    @Override
    public <T> T getEntity(Class<T> clazz, String id) {
        Object retrieved = firstCaches.getFirstCacheOrNull(clazz, id);

        if (retrieved == null) {
            T loaded = simpleEntityPersister.load(clazz, id);

            if (loaded == null) {
                return null;
            }

            firstCaches.putFirstCache(loaded, id);
            snapShots.putSnapShot(createDeepCopy(loaded), id);

            return clazz.cast(loaded);
        }
        return clazz.cast(retrieved);
    }

    @Override
    public <T> T addEntity(T instance) {
        EntityAttribute entityAttribute = entityAttributeCenter.findEntityAttribute(instance.getClass());
        IdAttribute idAttribute = entityAttribute.getIdAttribute();

        Field idField = idAttribute.getField();

        String instanceId = getInstanceIdAsString(instance, idField);

        if (isNewInstance(instanceId)) {
            return insert(instance, idAttribute);
        }

        T snapshot = getDatabaseSnapshot(instance, instanceId);

        T updated = simpleEntityPersister.update(snapshot, instance); // 나중에 쓰기지연 구현

        snapShots.putSnapShot(createDeepCopy(instance), instanceId);
        firstCaches.putFirstCache(instance, instanceId);

        return updated;
    }

    @Override
    public <T> void removeEntity(T instance) {
        EntityAttribute entityAttribute = entityAttributeCenter.findEntityAttribute(instance.getClass());
        Field idField = entityAttribute.getIdAttribute().getField();
        Class<?> clazz = instance.getClass();
        String instanceId = getInstanceIdAsString(instance, idField);

        firstCaches.remove(clazz, instanceId);
        snapShots.remove(clazz, instanceId);

        simpleEntityPersister.remove(instance, instanceId);
    }

    @Override
    public <T> T getDatabaseSnapshot(T instance, String instanceId) {
        Object snapshot = snapShots.getSnapShotOrNull(instance.getClass(), instanceId);

        if (snapshot == null) {
            Object loaded = simpleEntityPersister.load(instance.getClass(), instanceId);
            Object newSnapShot = createDeepCopy(loaded);
            snapShots.putSnapShot(newSnapShot, instanceId);
            return (T) newSnapShot;
        }
        return (T) snapshot;
    }

    private boolean isNewInstance(String instanceId) {
        return instanceId == null;
    }

    private <T> T insert(T instance, IdAttribute idAttribute) {
        assert idAttribute.getGenerationType() != null;

        T inserted = simpleEntityPersister.insert(instance);
        String insertedId = getInstanceIdAsString(instance, idAttribute.getField());

        firstCaches.putFirstCache(inserted, insertedId);
        snapShots.putSnapShot(createDeepCopy(inserted), insertedId);

        return inserted;
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
