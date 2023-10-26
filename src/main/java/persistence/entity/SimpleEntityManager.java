package persistence.entity;

import persistence.util.ReflectionUtils;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersisterProvider entityPersisterProvider;
    private final EntityLoaderProvider entityLoaderProvider;
    private final PersistenceContext persistenceContext;
    private final EntityKeyGenerator entityKeyGenerator;

    public SimpleEntityManager(final EntityPersisterProvider entityPersisterProvider, final EntityLoaderProvider entityLoaderProvider) {
        this.entityPersisterProvider = entityPersisterProvider;
        this.entityLoaderProvider = entityLoaderProvider;
        this.entityKeyGenerator = new EntityKeyGenerator();
        this.persistenceContext = new SimplePersistenceContext();
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long id) {
        final EntityLoader<T> entityLoader = entityLoaderProvider.getEntityLoader(clazz);
        final EntityKey entityKey = entityKeyGenerator.generate(clazz, id);
        final Object entity = persistenceContext.getEntity(entityKey)
                .orElseGet(() -> {
                    final Object entityFromDatabase = entityLoader.loadById(entityKey.getKey());
                    persistenceContext.addEntity(entityKey, entityFromDatabase);
                    persistenceContext.getDatabaseSnapshot(entityKey, entityFromDatabase);
                    return entityFromDatabase;
                });
        return clazz.cast(entity);
    }

    @Override
    public void persist(final Object entity) {
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());

        Object idValue = ReflectionUtils.getFieldValue(entity, entityPersister.getIdColumnName());
        if (Objects.isNull(idValue)) {
            entityPersister.insert(entity);
            idValue = ReflectionUtils.getFieldValue(entity, entityPersister.getIdColumnName());
        } else {
            entityPersister.update(entity);
        }

        persistenceContext.addEntity(entityKeyGenerator.generate(entity.getClass(), idValue), entity);
    }

    @Override
    public void remove(final Object entity) {
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());
        final Object idValue = ReflectionUtils.getFieldValue(entity, entityPersister.getIdColumnName());
        persistenceContext.removeEntity(entityKeyGenerator.generate(entity.getClass(), idValue));
        entityPersister.delete(entity);
    }

}
