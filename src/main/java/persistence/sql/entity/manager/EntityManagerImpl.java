package persistence.sql.entity.manager;

import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.context.PersistenceContext;
import persistence.sql.entity.loader.EntityLoader;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.persister.EntityPersister;

import java.util.List;

public class EntityManagerImpl implements EntityManager {

    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;


    public EntityManagerImpl(final EntityLoader entityLoader,
                             final EntityPersister entityPersister,
                             final PersistenceContext persistenceContext) {
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> List<T> findAll(final Class<T> clazz) {
        return entityLoader.findAll(clazz);
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        T persistenceEntity = persistenceContext.getEntity(clazz, id);
        if(persistenceEntity != null) {
            return persistenceEntity;
        }

        T entity = entityLoader.find(clazz, id);
        insertEntityLoader(entity, id);
        return entity;
    }

    private void insertEntityLoader(final Object entity, final Object id) {
        if(entity != null) {
            persistenceContext.addEntity(entity, id);
        }
    }

    @Override
    public void persist(final Object entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();
        final Object key = pkDomainType.getValue();

        Object cacheEntity = persistenceContext.getEntity(entity.getClass(), key);
        if(cacheEntity == null) {
            Object newKey = entityPersister.insertWithPk(entity);
            insertEntityLoader(entity, newKey);
        }
    }

    @Override
    public void remove(final Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    @Override
    public void removeAll(final Class<?> clazz) {
        entityPersister.deleteAll(clazz);
        persistenceContext.removeAll();
    }

    @Override
    public void merge(Object entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();
        final Object key = pkDomainType.getValue();

        Object snapshotEntity = persistenceContext.getDatabaseSnapshot(entity.getClass(), key);

        if(!entity.equals(snapshotEntity)) {
            entityPersister.update(entity);
            insertEntityLoader(entity, key);
        }
    }
}
