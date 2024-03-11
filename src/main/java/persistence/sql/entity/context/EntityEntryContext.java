package persistence.sql.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityEntryContext {

    private Map<Object, EntityEntry> entityStatusMap;

    public EntityEntryContext() {
        this.entityStatusMap = new ConcurrentHashMap<>();
    }

    public EntityEntry getEntityEntry(final Object entity) {
        return entityStatusMap.get(entity);
    }

    public void delete(final Object entity) {
        getEntityEntry(entity).deleted();
    }

    public void managed(final Object entity) {
        entityStatusMap.put(entity, new EntityEntry(entity, EntityStatus.MANAGED));
    }

    public void gone(final Object entity) {
        getEntityEntry(entity).gone();
    }

    public void saving(final Object entity) {
        if(getEntityEntry(entity) == null) {
            entityStatusMap.put(entity, new EntityEntry(entity, EntityStatus.SAVING));
            return;
        }
        getEntityEntry(entity).saving();
    }

    public void loading(final Object entity) {
        getEntityEntry(entity).loading();
    }

    public void readOnly(final Object entity) {
        getEntityEntry(entity).readOnly();
    }

    public void clear() {
        entityStatusMap.clear();
    }

}
