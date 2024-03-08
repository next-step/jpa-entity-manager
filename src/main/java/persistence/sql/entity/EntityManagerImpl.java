package persistence.sql.entity;

import persistence.sql.meta.PrimaryKey;
import persistence.sql.meta.simple.SimpleEntityMetaCreator;

import java.util.Objects;

import static persistence.sql.meta.simple.SimpleEntityMetaCreator.createPrimaryKeyValue;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(final EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long id) {
        final PrimaryKey key = SimpleEntityMetaCreator.createPrimaryKeyValue(clazz, id);

        if (Objects.isNull(persistenceContext.getEntity(key))) {
            final T instance = entityLoader.findById(clazz, id);
            persistenceContext.addEntity(key, instance);
            return instance;
        }

        return (T) persistenceContext.getEntity(key);
    }

    @Override
    public Object persist(final Object entity) {
        final Long id = entityPersister.insert(entity);
        final PrimaryKey key = SimpleEntityMetaCreator.createPrimaryKeyValue(entity.getClass(), id);

        persistenceContext.addEntity(key, entity);

        return entity;
    }

    @Override
    public Object merge(final Object entity) {
        final PrimaryKey key = createPrimaryKeyValue(entity);

        if (persistenceContext.isDirty(key, entity)) {
            entityPersister.update(entity);
        } else {
            entityPersister.insert(entity);
        }

        persistenceContext.addEntity(key, entity);
        return entityLoader.findById(entity.getClass(), (Long) key.value());
    }


    @Override
    public void remove(final Object entity) {
        final PrimaryKey key = createPrimaryKeyValue(entity);
        persistenceContext.removeEntity(key);
        entityPersister.delete(entity);
    }

    public boolean isDirty(Object entity) {
        final PrimaryKey key = createPrimaryKeyValue(entity);

        return persistenceContext.isDirty(key, entity);
    }
}
