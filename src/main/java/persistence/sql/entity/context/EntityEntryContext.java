package persistence.sql.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityEntryContext {

    private Map<EntityKey, EntityEntry> entityStatusMap;

    public EntityEntryContext() {
        this.entityStatusMap = new ConcurrentHashMap<>();
    }

    public EntityEntry getEntityEntry(final EntityKey entityKey) {
        return entityStatusMap.get(entityKey);
    }

    public void delete(final EntityKey entityKey) {
        getEntityEntry(entityKey).deleted();
    }

    public void managed(final EntityKey entityKey) {
        entityStatusMap.put(entityKey, new EntityEntry(EntityStatus.MANAGED));
    }

    public void gone(final EntityKey entityKey) {
        getEntityEntry(entityKey).gone();
    }

    public void saving(final EntityKey entityKey) {
        getEntityEntry(entityKey).saving();
    }

    public void loading(final EntityKey entityKey) {
        getEntityEntry(entityKey).loading();
    }

    public void readOnly(final EntityKey entityKey) {
        getEntityEntry(entityKey).readOnly();
    }

    public void clear() {
        entityStatusMap.clear();
    }

}
