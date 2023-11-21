package persistence.entity.entry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.entity.context.PersistenceContext;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;

public class EntityEntry {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;
    private final Map<Integer, EntityStatus> entityStatusMap = new ConcurrentHashMap<>();

    public EntityEntry(EntityPersister entityPersister, EntityLoader entityLoader, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    public <T> T find(Class<T> clazz, Long id) {
        if (id == null || clazz == null) {
            throw new NullPointerException("Id can't be null in EntityManager find");
        }
        Object persistenceContextCachedEntity = persistenceContext.getEntity(clazz, id);
        if (persistenceContextCachedEntity == null) {
            Object entity = entityLoader.find(clazz, id);
            if (entity != null) {
                int code = System.identityHashCode(entity);
                entityStatusMap.put(code, EntityStatus.LOADING);
                persistenceContext.addEntity(id, entity);
                entityStatusMap.put(code, EntityStatus.MANAGED);
                return (T) entity;
            }
            return null;
        }
        int code = System.identityHashCode(persistenceContextCachedEntity);
        if (EntityStatus.readAble(entityStatusMap.get(code))) {
            return (T) persistenceContextCachedEntity;
        }
        throw new IllegalStateException("Object is not readable");
    }

    public Object update(Object entity, Long idValue) {
        if (entity == null) {
            throw new NullPointerException("Can not be update with null");
        }
        int code = System.identityHashCode(entity);
        if (!EntityStatus.updateAble(entityStatusMap.get(code))) {
            throw new IllegalStateException("Can not be update");
        }
        Object snapshotEntity = persistenceContext.getDatabaseSnapshot(entity.getClass(), idValue);
        if (entity.equals(snapshotEntity)) {
            return entity;
        }
        entityPersister.update(entity);
        persistenceContext.addEntity(idValue, entity);
        return entity;
    }

    public Long insert(Object object) {
        if (object == null) {
            throw new NullPointerException("insert with null");
        }
        int code = System.identityHashCode(object);
        if (!EntityStatus.insertAble(entityStatusMap.get(code))) {
            throw new IllegalStateException("Can not be insert");
        }
        entityStatusMap.put(code, EntityStatus.SAVING);
        Long id = entityPersister.insert(object);
        persistenceContext.addEntity(id, object);
        entityStatusMap.put(code, EntityStatus.MANAGED);
        return id;
    }

    public void delete(Object object) {
        if (object == null) {
            throw new NullPointerException("delete with null");
        }
        int code = System.identityHashCode(object);
        entityPersister.delete(object);
        entityStatusMap.put(code, EntityStatus.DELETED);
        persistenceContext.removeEntity(object);
        entityStatusMap.put(code, EntityStatus.GONE);
    }
}
