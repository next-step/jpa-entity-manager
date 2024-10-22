package persistence.sql.dml.impl;

import jakarta.persistence.EntityExistsException;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.clause.Clause;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Field;
import java.util.List;

public class DefaultEntityManager implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoaderFactory entityLoaderFactory;

    public DefaultEntityManager(PersistenceContext persistenceContext, EntityPersister entityPersister) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
        this.entityLoaderFactory = EntityLoaderFactory.getInstance();
    }

    @Override
    public <T> T persist(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        if (!isNew(entity)) {
            throw new EntityExistsException("Entity already exists");
        }

        Object id = entityPersister.insert(entity);
        persistenceContext.add(id, entity);

        return entity;
    }

    private <T> boolean isNew(Object entity) {
        try {
            EntityLoader<?> entityLoader = entityLoaderFactory.getLoader(entity.getClass());
            MetadataLoader<?> loader = entityLoader.getMetadataLoader();

            Field primaryKeyField = loader.getPrimaryKeyField();
            primaryKeyField.setAccessible(true);
            Object idValue = primaryKeyField.get(entity);
            if (idValue == null) {
                return true;
            }

            return find(loader.getEntityType(), idValue) == null;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    @Override
    public <T> T merge(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        MetadataLoader<?> loader = new SimpleMetadataLoader<>(entity.getClass());

        if (isNew(entity)) {
            return persist(entity);
        }

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        entityPersister.update(entity);
        persistenceContext.merge(id, entity);

        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        entityPersister.delete(entity);
        persistenceContext.delete(entity);
    }

    @Override
    public <T> T find(Class<T> returnType, Object primaryKey) {
        if (primaryKey == null) {
            throw new IllegalArgumentException("Primary key must not be null");
        }

        T foundEntity = persistenceContext.get(returnType, primaryKey);

        if (foundEntity != null) {
            return foundEntity;
        }

        EntityLoader<T> entityLoader = entityLoaderFactory.getLoader(returnType);

        return entityLoader.load(primaryKey);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        EntityLoader<T> entityLoader = entityLoaderFactory.getLoader(entityClass);

        return entityLoader.loadAll();
    }
}
