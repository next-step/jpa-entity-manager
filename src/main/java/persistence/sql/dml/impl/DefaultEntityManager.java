package persistence.sql.dml.impl;

import jakarta.persistence.EntityExistsException;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.clause.Clause;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.loader.EntityLoader;
import persistence.sql.transaction.Transaction;
import persistence.sql.transaction.impl.EntityTransaction;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.List;

public class DefaultEntityManager implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoaderFactory entityLoaderFactory;
    private Transaction transaction;


    public DefaultEntityManager(PersistenceContext persistenceContext, EntityPersister entityPersister) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = entityPersister;
        this.entityLoaderFactory = EntityLoaderFactory.getInstance();
        this.transaction = new EntityTransaction(this);
    }

    @Override
    public Transaction getTransaction() {
        Connection connection = entityPersister.getConnection();
        transaction.connect(connection);
        return transaction;
    }

    @Override
    public <T> void persist(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }

        if (!isNew(entity)) {
            throw new EntityExistsException("Entity already exists");
        }

        Object id = entityPersister.insert(entity);
        persistenceContext.add(id, entity);
        persistenceContext.createDatabaseSnapshot(id, entity);
    }

    private boolean isNew(Object entity) {
        EntityLoader<?> entityLoader = entityLoaderFactory.getLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        Field primaryKeyField = loader.getPrimaryKeyField();
        Object idValue = Clause.extractValue(primaryKeyField, entity);
        if (idValue == null) {
            return true;
        }

        return find(loader.getEntityType(), idValue) == null;
    }

    @Override
    public <T> T merge(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        EntityLoader<?> entityLoader = entityLoaderFactory.getLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        if (isNew(entity)) {
            persist(entity);
            return entity;
        }

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        T databaseSnapshot = persistenceContext.getDatabaseSnapshot(id, entity);

        entityPersister.update(entity, databaseSnapshot);
        persistenceContext.add(id, entity);
        persistenceContext.updateSnapshot(id, entity);

        return entity;
    }

    @Override
    public <T> void remove(T entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null");
        }
        EntityLoader<?> entityLoader = entityLoaderFactory.getLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);

        entityPersister.delete(entity);
        persistenceContext.delete(entity, id);
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

        T loadedEntity = entityLoader.load(primaryKey);
        if (loadedEntity != null) {
            persistenceContext.add(primaryKey, loadedEntity);
            persistenceContext.createDatabaseSnapshot(primaryKey, loadedEntity);
        }

        return loadedEntity;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        EntityLoader<T> entityLoader = entityLoaderFactory.getLoader(entityClass);

        List<T> loadedEntities = entityLoader.loadAll();
        loadedEntities.forEach(entity -> persistenceContext.add(
                Clause.extractValue(entityLoader.getMetadataLoader().getPrimaryKeyField(), entity), entity));
        return loadedEntities;
    }

    @Override
    public void onFlush() {
        if (persistenceContext.isDirty()) {
            persistenceContext.getDirtyEntities().forEach(this::merge);
        }
        persistenceContext.cleanup();
    }
}
