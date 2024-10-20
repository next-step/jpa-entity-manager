package persistence.sql.dml.impl;

import persistence.sql.clause.Clause;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;

import java.lang.reflect.Field;
import java.util.List;
import java.util.logging.Logger;

public class DefaultEntityManager implements EntityManager {
    private static final Logger logger = Logger.getLogger(DefaultEntityManager.class.getName());

    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;

    public DefaultEntityManager(PersistenceContext persistenceContext, EntityPersister entityPersister) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> void persist(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        MetadataLoader<?> loader = new SimpleMetadataLoader<>(entity.getClass());

        if (!isNew(entity, loader)) {
            merge(entity);
            return;
        }

        Object id = entityPersister.insert(entity, loader);
        updatePrimaryKeyValue(entity, id, loader);
        persistenceContext.add(id, entity);
    }

    private <T> boolean isNew(Object entity, MetadataLoader<T> loader) {
        try {
            Field primaryKeyField = loader.getPrimaryKeyField();
            primaryKeyField.setAccessible(true);
            Object idValue = primaryKeyField.get(entity);
            if (idValue == null) {
                return true;
            }

            return find(loader.getEntityType(), idValue) == null;
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    @Override
    public <T> void merge(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        MetadataLoader<?> loader = new SimpleMetadataLoader<>(entity.getClass());

        if (isNew(entity, loader)) {
            persist(entity);
            return;
        }

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        entityPersister.update(entity, loader);
        persistenceContext.merge(id, loader);
    }

    @Override
    public <T> void remove(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        MetadataLoader<?> loader = new SimpleMetadataLoader<>(entity.getClass());

        entityPersister.delete(entity, loader);
        persistenceContext.delete(entity);
    }

    @Override
    public <T> T find(Class<T> returnType, Object primaryKey) {
        if (primaryKey == null) {
            throw new IllegalArgumentException("Primary key must not be null");
        }

        MetadataLoader<T> loader = new SimpleMetadataLoader<>(returnType);
        return entityPersister.select(returnType, primaryKey, loader);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        MetadataLoader<T> loader = new SimpleMetadataLoader<>(entityClass);

        return entityPersister.selectAll(entityClass, loader);
    }

    private void updatePrimaryKeyValue(Object entity, Object id, MetadataLoader<?> loader) {
        Field primaryKeyField = loader.getPrimaryKeyField();
        primaryKeyField.setAccessible(true);

        try {
            primaryKeyField.set(entity, id);
        } catch (IllegalAccessException e) {
            logger.severe("Failed to set primary key value");
            throw new RuntimeException(e);
        }
    }
}
