package persistence.entity.entry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;

public class EntityEntry {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final Map<Integer, EntityStatus> entityStatusMap = new ConcurrentHashMap<>();

    public EntityEntry(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    public <T> T find(Class<T> clazz, Long id) {
        T result = entityLoader.find(clazz, id);
        if (result != null) {
            entityStatusMap.put(System.identityHashCode(result), EntityStatus.MANAGED);
        }
        return result;
    }

    public boolean update(Object object) {
        if (object == null) {
            throw new NullPointerException("Can not be update with null");
        }
        int code = System.identityHashCode(object);
        if (!EntityStatus.updateAble(entityStatusMap.get(code))) {
            throw new IllegalStateException("Can not be update");
        }
        return entityPersister.update(object);
    }

    public Long insert(Object object) {
        int code = System.identityHashCode(object);
        if (!EntityStatus.insertAble(entityStatusMap.get(code))) {
            throw new IllegalStateException("Can not be insert");
        }
        entityStatusMap.put(code, EntityStatus.SAVING);
        Long result = entityPersister.insert(object);
        entityStatusMap.put(code, EntityStatus.MANAGED);
        return result;
    }

    public void delete(Object object) {
        int code = System.identityHashCode(object);
        entityPersister.delete(object);
        entityStatusMap.put(code, EntityStatus.DELETED);
    }
}
