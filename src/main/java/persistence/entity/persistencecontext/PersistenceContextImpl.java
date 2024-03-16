package persistence.entity.persistencecontext;

import persistence.PrimaryKey;
import persistence.entity.exception.NotHandledEntityEntryException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static persistence.entity.persistencecontext.Status.SAVING;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, EntityEntry> entityEntries;
    private final EntityCache entityCache;
    private final Snapshot snapshot;

    public PersistenceContextImpl() {
        this.entityEntries = new HashMap<>();
        this.entityCache = new EntityCache();
        this.snapshot = new Snapshot();
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<Object> cachedEntity = entityCache.get(new EntityKey(clazz, id));
        if (cachedEntity.isPresent()) {
            return (Optional<T>) cachedEntity;
        }
        return Optional.empty();
    }

    @Override
    public <T> T addEntity(T entity, Long id) {
        Class<?> clazz = entity.getClass();
        EntityKey entityKey = new EntityKey(clazz, id);
        entityCache.put(entity, entityKey);
        return entity;
    }

    @Override
    public <T> T updateEntity(T entity, Long id) {
        EntityKey key = new EntityKey(entity.getClass(), id);
        entityCache.put(entity, key);
        snapshot.put(entity, key);
        return entity;
    }

    @Override
    public void removeEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        Long id = new PrimaryKey(entity.getClass()).getPrimaryKeyValue(entity);
        EntityKey entityKey = new EntityKey(clazz, id);
        entityCache.remove(entityKey);
        snapshot.remove(entityKey);
    }

    @Override
    public <T> T getDatabaseSnapshot(T entity, Long id) {
        EntityKey key = new EntityKey(entity.getClass(), id);
        return snapshot.get(key);
    }

    @Override
    public Optional<EntityEntry> getEntityEntry(Class<?> clazz, Long id) {
        EntityEntry entityEntry = this.entityEntries.get(new EntityKey(clazz, id));
        if(entityEntry==null) {
            return Optional.empty();
        }
        return Optional.of(entityEntry);
    }

    @Override
    public void addEntityEntry(Class<?> clazz, Long id) {
        this.entityEntries.put(new EntityKey(clazz, id), new EntityEntry());
    }

    @Override
    public <T> void addEntityEntry(T entity) {
        this.entityEntries.put(new EntityKey(entity), new EntityEntry());
    }

    @Override
    public <T> void saveEntryEntity(T entity) {
        EntityEntry entityEntry = this.getEntityEntry(entity);
        entityEntry.updateStatus(SAVING); // TODO: 동일한 객체로 있는지 확인 (entryEntity)
        this.entityEntries.put(new EntityKey(entity), entityEntry); // TODO: PUT 필요할까?
    }
    
    private <T> EntityEntry getEntityEntry(T entity) {
        EntityKey key = new EntityKey(entity);
        EntityEntry entityEntry = this.entityEntries.get(key);
        if (entityEntry == null) {
            throw new NotHandledEntityEntryException(key);
        }
        return entityEntry;
    }
}
