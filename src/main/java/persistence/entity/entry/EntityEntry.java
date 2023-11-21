package persistence.entity.entry;

import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;

public class EntityEntry {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityEntry(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    public <T> T find(Class<T> clazz, Long id) {
        return entityLoader.find(clazz, id);
    }

    public boolean update(Object object) {
        return entityPersister.update(object);
    }

    public Long insert(Object object) {
        return entityPersister.insert(object);
    }

    public void delete(Object object) {
        entityPersister.delete(object);
    }

}
