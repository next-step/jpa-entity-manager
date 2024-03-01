package persistence.entity;

import jdbc.RowMapper;
import persistence.Person;

public class EntityMangerImpl implements EntityManger {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityMangerImpl(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityLoader.find(clazz, id);
    }

    @Override
    public Object persist(Object entity) {
        boolean isEntityUpdated = entityPersister.update(entity);
        if(!isEntityUpdated){
            entityPersister.insert(entity);
        }

        return entity;
    }


    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
