package persistence.sql.entity.manager;

import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.context.PersistenceContext;
import persistence.sql.entity.context.PersistenceContextImpl;
import persistence.sql.entity.exception.ReadOnlyException;
import persistence.sql.entity.exception.RemoveEntityException;
import persistence.sql.entity.loader.EntityLoader;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.persister.EntityPersister;

import java.util.List;

public class EntityManagerImpl implements EntityManager {

    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;


    public EntityManagerImpl(final EntityLoader entityLoader,
                             final EntityPersister entityPersister) {
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> List<T> findAll(final Class<T> clazz) {
        return entityLoader.findAll(clazz);
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        T persistenceEntity = persistenceContext.getEntity(clazz, id);
        if(persistenceContext.isGone(clazz,id)) {
            throw new RemoveEntityException();
        }

        if(persistenceEntity != null) {
            persistenceContext.loading(persistenceEntity, id);
            return persistenceEntity;
        }

        if(persistenceContext.isGone(clazz,id)) {
            throw new RemoveEntityException();
        }

        T entity = entityLoader.find(clazz, id);
        if(entity != null) {
            insertEntityLoader(entity, id);
            persistenceContext.loading(entity, id);
        }
        return entity;
    }

    @Override
    public <T> T findOfReadOnly(Class<T> clazz, Object id) {
        T persistenceEntity = persistenceContext.getEntity(clazz, id);
        if(persistenceEntity != null) {
            persistenceContext.readOnly(persistenceEntity, id);
            return persistenceEntity;
        }

        if(persistenceContext.isGone(clazz,id)) {
            throw new RemoveEntityException();
        }

        T entity = entityLoader.find(clazz, id);
        if(entity != null) {
            insertEntityLoader(entity, id);
            persistenceContext.readOnly(entity, id);
        }
        return entity;
    }

    @Override
    public void persist(final Object entity) {
        if(persistenceContext.isReadOnly(entity)) {
            throw new ReadOnlyException();
        }

        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();
        final Object key = pkDomainType.getValue();

        final Object cacheEntity = persistenceContext.getEntity(entity.getClass(), key);
        if(cacheEntity == null) {
            insertEntity(entity);
        }

        final Object snapshotEntity = persistenceContext.getDatabaseSnapshot(entity.getClass(), key);
        if(!entity.equals(snapshotEntity)) {
            updateEntity(entity, key);
            persistenceContext.saving(entity);
        }
    }

    private void insertEntityLoader(final Object entity, final Object id) {
        if(entity != null) {
            persistenceContext.addEntity(entity, id);
        }
    }

    private void insertEntity(final Object entity) {
        Object newKey = entityPersister.insertWithPk(entity);
        insertEntityLoader(entity, newKey);
    }

    private void updateEntity(final Object entity, final Object key) {
        entityPersister.update(entity);
        insertEntityLoader(entity, key);
    }

    @Override
    public void remove(final Object entity) {
        if(persistenceContext.isReadOnly(entity)) {
            throw new ReadOnlyException();
        }

        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
        persistenceContext.goneEntity(entity);
    }

    @Override
    public void removeAll(final Class<?> clazz) {
        entityPersister.deleteAll(clazz);
        persistenceContext.removeAll();
    }
}
