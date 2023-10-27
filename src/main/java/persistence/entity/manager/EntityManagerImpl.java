package persistence.entity.manager;

import persistence.context.PersistenceContext;
import persistence.entity.attribute.EntityAttribute;
import persistence.entity.loader.EntityLoaderImpl;
import persistence.entity.persister.EntityPersister;

import java.lang.reflect.Field;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;
    private final EntityLoaderImpl entityLoaderImpl;

    private EntityManagerImpl(EntityPersister entityPersister,
                              EntityLoaderImpl entityLoaderImpl,
                              PersistenceContext persistenceContext) {
        this.entityLoaderImpl = entityLoaderImpl;
        this.entityPersister = entityPersister;
        this.persistenceContext = persistenceContext;
    }

    public static EntityManagerImpl of(EntityPersister entityPersister,
                                       EntityLoaderImpl entityLoaderImpl,
                                       PersistenceContext persistenceContext) {
        return new EntityManagerImpl(entityPersister, entityLoaderImpl, persistenceContext);
    }

    @Override
    public <T> T findById(Class<T> clazz, String id) {
        T retrieved = persistenceContext.getEntity(clazz, id);

        if (retrieved == null) {
            return loadAndManageEntity(clazz, id);
        }

        return retrieved;
    }

    @Override
    public <T> T persist(T instance) {
        EntityAttribute entityAttribute = EntityAttribute.of(instance.getClass());

        T inserted = entityPersister.insert(instance);

        String instanceId = getInstanceId(instance, entityAttribute.getIdAttribute().getField());
        persistenceContext.addEntity(inserted, instanceId);

        return inserted;
    }

    @Override
    public <T> void remove(T instance) {
        EntityAttribute entityAttribute = EntityAttribute.of(instance.getClass());
        String instanceId = getInstanceId(instance, entityAttribute.getIdAttribute().getField());

        persistenceContext.removeEntity(instance, instanceId);

        entityPersister.remove(instance, instanceId);
    }

    private <T> T loadAndManageEntity(Class<T> clazz, String id) {
        return entityLoaderImpl.load(clazz, id);
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
}
