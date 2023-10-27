package persistence.entity;

import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersisterProvider entityPersisterProvider;
    private final EntityLoaderProvider entityLoaderProvider;
    private final PersistenceContext persistenceContext;
    private final EntityKeyGenerator entityKeyGenerator;

    public SimpleEntityManager(final EntityPersisterProvider entityPersisterProvider, final EntityLoaderProvider entityLoaderProvider, final EntityKeyGenerator entityKeyGenerator) {
        this.entityPersisterProvider = entityPersisterProvider;
        this.entityLoaderProvider = entityLoaderProvider;
        this.entityKeyGenerator = entityKeyGenerator;
        this.persistenceContext = new SimplePersistenceContext();
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        final EntityLoader<T> entityLoader = entityLoaderProvider.getEntityLoader(clazz);
        final EntityKey entityKey = entityKeyGenerator.generate(clazz, id);
        final Object entity = persistenceContext.getEntity(entityKey)
                .orElseGet(() -> initEntity(entityKey, entityLoader));
        return clazz.cast(entity);
    }

    @Override
    public void persist(final Object entity) {
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());

        final Object idValue = extractId(entity, entityPersister);
        final boolean hasIdValue = !Objects.isNull(idValue);
        if (hasIdValue) {
            checkEntityAlreadyExists(entity, idValue);
            processUpdate(entity, entityPersister);
            return;
        }

        processInsert(entity, entityPersister);
    }

    @Override
    public <T> T merge(final T entity) {
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());

        final Object idValue = extractId(entity, entityPersister);
        final boolean isIdValueNull = Objects.isNull(idValue);
        if (isIdValueNull) {
            throw new PersistenceException("Id value 없이 merge 할 수 없습니다.");
        }

        processUpdate(entity, entityPersister);
        return entity;
    }

    @Override
    public void remove(final Object entity) {
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());
        final Object idValue = extractId(entity, entityPersister);

        persistenceContext.removeEntity(entityKeyGenerator.generate(entity.getClass(), idValue));
        entityPersister.delete(entity);
    }

    private Object extractId(final Object entity, final EntityPersister entityPersister) {
        return ReflectionUtils.getFieldValue(entity, entityPersister.getIdColumnFieldName());
    }

    private <T> Object initEntity(final EntityKey entityKey, final EntityLoader<T> entityLoader) {
        final Object entityFromDatabase = entityLoader.loadById(entityKey.getKey()).orElseThrow(() -> new PersistenceException("존재하지 않는 entity 입니다."));
        persistenceContext.addEntity(entityKey, entityFromDatabase);
        persistenceContext.getDatabaseSnapshot(entityKey, entityFromDatabase);
        return entityFromDatabase;
    }

    private void checkEntityAlreadyExists(final Object entity, final Object idValue) {
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);
        if (persistenceContext.hasEntity(entityKey)) {
            throw new PersistenceException("이미 persistence context에 존재하는 entity 는 persist 할 수 없습니다.");
        }
    }

    private void processInsert(final Object entity, final EntityPersister entityPersister) {
        entityPersister.insert(entity);
        final Object idValue = extractId(entity, entityPersister);
        persistenceContext.addEntity(entityKeyGenerator.generate(entity.getClass(), idValue), entity);
    }

    private void processUpdate(final Object entity, final EntityPersister entityPersister) {
        final Object idValue = extractId(entity, entityPersister);
        final EntityKey entityKey = entityKeyGenerator.generate(entity.getClass(), idValue);

        if (!persistenceContext.hasEntity(entityKey)) {
            find(entity.getClass(), idValue);
        }

        if (isDirty(entity)) {
            entityPersister.update(entity);
            persistenceContext.addEntity(entityKey, entity);
        }
    }

    private boolean isDirty(final Object entity) {
        final EntityPersister entityPersister = entityPersisterProvider.getEntityPersister(entity.getClass());
        final Object idValue = extractId(entity, entityPersister);
        final Object cachedDatabaseSnapshot = persistenceContext.getCachedDatabaseSnapshot(entityKeyGenerator.generate(entity.getClass(), idValue));

        return entityPersister.getColumnFieldNames()
                .stream()
                .anyMatch(columnName -> hasDifferentValue(entity, cachedDatabaseSnapshot, columnName));
    }

    private boolean hasDifferentValue(final Object entity, final Object cachedDatabaseSnapshot, final String columnName) {
        final Object targetValue = ReflectionUtils.getFieldValue(entity, columnName);
        final Object snapshotValue = ReflectionUtils.getFieldValue(cachedDatabaseSnapshot, columnName);
        return !Objects.equals(targetValue, snapshotValue);
    }

}
