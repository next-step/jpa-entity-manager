package persistence.sql.entity.manager;

import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.loader.EntityLoader;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.persister.EntityPersister;

import java.util.List;

public class EntityManagerImpl<T> implements EntityManger<T> {

    private final EntityLoader<T> entityLoader;
    private final EntityPersister<T> entityPersister;


    public EntityManagerImpl(final EntityLoader<T> entityLoader,
                             final EntityPersister<T> entityPersister) {
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
    }

    @Override
    public List<T> findAll(final Class<T> clazz) {
        return entityLoader.findAll(clazz);
    }

    @Override
    public T find(final Class<T> clazz, final Object id) {
        return entityLoader.find(clazz, id);
    }

    @Override
    public void persist(final T entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();
        final Object key = pkDomainType.getValue();

        final T object = find((Class<T>) entity.getClass(), key);

        if (object == null) {
            entityPersister.insert(entity);
            return;
        }
        entityPersister.update(entity);
    }

    @Override
    public void remove(final T entity) {
        entityPersister.delete(entity);
    }

    @Override
    public void removeAll(final Class<T> clazz) {
        entityPersister.deleteAll();
    }
}
