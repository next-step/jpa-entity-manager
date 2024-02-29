package persistence.persistencecontext;

import persistence.entity.EntityMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MyPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, EntityMeta> entities = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> snapshots = new HashMap<>();

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Object id) {
        EntityKey entityKey = new EntityKey(id, clazz);
        return Optional.ofNullable(entities.get(entityKey))
                .map(EntityMeta::getEntity)
                .map(clazz::cast);
    }

    @Override
    public void addEntity(EntityMeta entityMeta) {
        entities.put(entityMeta.getEntityKey(), entityMeta);
    }

    @Override
    public void removeEntity(EntityMeta entityMeta) {
        entities.remove(entityMeta.getEntityKey());
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(EntityMeta entityMeta) {
        EntityKey entityKey = new EntityKey(entityMeta.getId(), entityMeta.getEntity().getClass());
        return snapshots.put(entityKey, EntitySnapshot.from(entityMeta.getEntity()));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(EntityMeta entityMeta) {
        EntityKey entityKey = new EntityKey(entityMeta.getId(), entityMeta.getEntity().getClass());
        return snapshots.get(entityKey);
    }
}
