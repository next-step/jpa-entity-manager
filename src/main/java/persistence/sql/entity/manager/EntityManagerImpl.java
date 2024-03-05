package persistence.sql.entity.manager;

import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.loader.EntityLoader;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.persister.EntityPersister;

import java.util.List;

public class EntityManagerImpl implements EntityManager {

    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;


    public EntityManagerImpl(final EntityLoader entityLoader,
                             final EntityPersister entityPersister) {
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> List<T> findAll(final Class<T> clazz) {
        return entityLoader.findAll(clazz);
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        return entityLoader.find(clazz, id);
    }

    @Override
    public void persist(final Object entity) {
        final EntityMappingTable entityMappingTable = EntityMappingTable.of(entity.getClass(), entity);
        final DomainType pkDomainType = entityMappingTable.getPkDomainTypes();
        final Object key = pkDomainType.getValue();

        final Object object = find(entity.getClass(), key);

        if (object == null) {
            entityPersister.insert(entity);
            return;
        }
        entityPersister.update(entity);
    }

    @Override
    public void remove(final Object entity) {
        entityPersister.delete(entity);
    }

    @Override
    public void removeAll(final Class<?> clazz) {
        entityPersister.deleteAll(clazz);
    }
}
