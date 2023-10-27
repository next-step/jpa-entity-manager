package persistence.entity.manager;

import persistence.context.PersistenceContext;
import persistence.entity.attribute.EntityAttribute;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;

    private EntityManagerImpl(
            PersistenceContext persistenceContext
    ) {
        this.persistenceContext = persistenceContext;
    }

    public static EntityManagerImpl of(
            PersistenceContext persistenceContext
    ) {
        return new EntityManagerImpl(persistenceContext);
    }

    @Override
    public <T> T findById(Class<T> clazz, String id) {
        return persistenceContext.getEntity(clazz, id);
    }

    @Override
    public <T> T persist(T instance) {
        EntityAttribute entityAttribute = EntityAttribute.of(instance.getClass());
        return persistenceContext.addEntity(instance, entityAttribute.getIdAttribute());
    }

    @Override
    public <T> void remove(T instance) {
        EntityAttribute entityAttribute = EntityAttribute.of(instance.getClass());

        persistenceContext.removeEntity(instance, entityAttribute.getIdAttribute().getField());
    }
}
