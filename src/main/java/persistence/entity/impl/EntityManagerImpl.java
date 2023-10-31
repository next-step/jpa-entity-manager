package persistence.entity.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityManager;
import persistence.entity.PersistenceContext;
import persistence.entity.impl.retrieve.EntityLoader;
import persistence.entity.impl.retrieve.EntityLoaderImpl;
import persistence.entity.impl.store.EntityPersister;
import persistence.entity.impl.store.EntityPersisterImpl;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityObjectMappingMeta;

public class EntityManagerImpl implements EntityManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ColumnType columnType;
    private final Connection connection;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(Connection connection, ColumnType columnType) {
        this.connection = connection;
        this.columnType = columnType;
        this.entityLoader = new EntityLoaderImpl(connection, columnType);
        this.entityPersister = new EntityPersisterImpl(connection);
        this.persistenceContext = new DefaultPersistenceContextImpl(columnType);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final Optional<Object> cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity.isEmpty()) {
            final T loadedEntity = entityLoader.load(clazz, id);
            syncToPersistenceContext(id, loadedEntity);

            return loadedEntity;
        }

        return clazz.cast(cachedEntity.get());
    }

    @Override
    public Object persist(Object entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        final Optional<Object> cachedEntity = persistenceContext.getEntity(entity.getClass(), objectMappingMeta.getIdValue());

        if (cachedEntity.isEmpty()) {
            final Object savedEntity = entityPersister.store(entity, columnType);
            final EntityObjectMappingMeta savedObjectMappingMeta = EntityObjectMappingMeta.of(savedEntity, columnType);
            syncToPersistenceContext(savedObjectMappingMeta.getIdValue(), savedEntity);

            return savedEntity;
        }

        return cachedEntity.get();
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        persistenceContext.purgeEntityCache(entity);
        entityPersister.delete(entity, columnType);
    }

    @Override
    public <T> T merge(T entity) {
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        final SnapShot snapShot = persistenceContext.getSnapShot(entity.getClass(), objectMappingMeta.getIdValue());
        if (snapShot.isSameWith(objectMappingMeta)) {
            return entity;
        }

        entityPersister.update(entity, columnType);
        syncToPersistenceContext(objectMappingMeta.getIdValue(), entity);

        return entity;
    }

    private void syncToPersistenceContext(Object id, Object entity) {
        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);
    }

    @Override
    public void clear() {
        persistenceContext.clearContextCache();
    }

    @Override
    public void close() {
        try {
            if (this.connection == null) {
                return;
            }

            if (this.connection.isClosed()) {
                return;
            }

            this.clear();
            this.connection.close();
            log.info("EntityManager closed");
        } catch (SQLException e) {
            log.error("EntityManager connection not closed", e);
            throw new RuntimeException(e);
        }
    }
}
