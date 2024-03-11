package persistence.sql.entity.context;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class EntityEntryContext {

    private List<EntityEntry> entities;

    public EntityEntryContext() {
        this.entities = new CopyOnWriteArrayList<>();
    }

    public EntityEntry getEntityEntry(final Object entity) {
        return entities.stream()
                .filter(entityEntry -> entityEntry.isEquals(entity))
                .findFirst()
                .orElseGet(() -> null);
    }

    public void delete(final Object entity) {
        getEntityEntry(entity).deleted();
    }

    public void managed(final Object entity) {
        entities.add(new EntityEntry(entity, EntityStatus.MANAGED));
    }

    public void gone(final Object entity) {
        getEntityEntry(entity).gone();
    }

    public void saving(final Object entity) {
        if (getEntityEntry(entity) == null) {
            entities.add(new EntityEntry(entity, EntityStatus.SAVING));
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
        entities.clear();
    }

}
