package persistence.context;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.attribute.EntityAttributeCenter;
import persistence.entity.attribute.id.IdAttribute;
import persistence.entity.entry.EntityEntries;
import persistence.entity.entry.Status;
import persistence.entity.persister.SimpleEntityPersister;

import java.lang.reflect.Field;
import java.util.Optional;

import static persistence.entity.entry.Status.*;

public class PersistenceContextImpl implements PersistenceContext {
    private final EntityAttributeCenter entityAttributeCenter;
    private final SimpleEntityPersister simpleEntityPersister;
    private final EntityEntries entityEntries = new EntityEntries();
    private final FirstCaches firstCaches = new FirstCaches();
    private final SnapShots snapShots = new SnapShots();

    public PersistenceContextImpl(SimpleEntityPersister simpleEntityPersister, EntityAttributeCenter entityAttributeCenter) {
        this.simpleEntityPersister = simpleEntityPersister;
        this.entityAttributeCenter = entityAttributeCenter;
    }

    @Override
    public <T> T getEntity(Class<T> clazz, String id) {
        Status currentStatus = entityEntries.getEntityEntry(clazz, id).getStatus();

        if (currentStatus == MANAGED) {
            return clazz.cast(firstCaches.getFirstCacheOrNull(clazz, id));
        }

        T loaded = simpleEntityPersister.load(clazz, id);

        if (loaded == null) {
            return null;
        }

        firstCaches.putFirstCache(loaded, id);
        snapShots.putSnapShot(loaded, id);
        entityEntries.changeOrSetStatus(MANAGED, clazz, id);

        return clazz.cast(loaded);
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

        snapShots.putSnapShot(instance, instanceId);
        firstCaches.putFirstCache(instance, instanceId);
        entityEntries.changeOrSetStatus(MANAGED, instance.getClass(), instanceId);

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
        entityEntries.changeOrSetStatus(DELETED, instance.getClass(), instanceId);

        simpleEntityPersister.remove(instance, instanceId);
        entityEntries.changeOrSetStatus(GONE, instance.getClass(), instanceId);
    }

    @Override
    public <T> T getDatabaseSnapshot(T instance, String instanceId) {
        Object snapshot = snapShots.getSnapShotOrNull(instance.getClass(), instanceId);

        if (snapshot == null) {
            entityEntries.changeOrSetStatus(LOADING, instance.getClass(), instanceId);
            Object loaded = simpleEntityPersister.load(instance.getClass(), instanceId);

            T newSnapShot = (T) snapShots.putSnapShot(loaded, instanceId);
            entityEntries.changeOrSetStatus(MANAGED, instance.getClass(), instanceId);
            return newSnapShot;
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
        snapShots.putSnapShot(inserted, insertedId);
        entityEntries.changeOrSetStatus(MANAGED, instance.getClass(), insertedId);

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
}
