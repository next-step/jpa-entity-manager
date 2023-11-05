package persistence.entity.impl.context;

import java.util.HashMap;
import java.util.Map;
import persistence.entity.EntityEntry;
import persistence.entity.impl.ImmutableEntityEntry;
import persistence.entity.type.EntityStatus;

public class EntityEntryContext {

    private static final EntityEntry LOADING_ENTITY_ENTRY = ImmutableEntityEntry.of(EntityStatus.LOADING);
    private static final EntityEntry SAVING_ENTITY_ENTRY = ImmutableEntityEntry.of(EntityStatus.SAVING);
    private static final EntityEntry MANAGED_ENTITY_ENTRY = ImmutableEntityEntry.of(EntityStatus.MANAGED);
    private static final EntityEntry READ_ONLY_ENTITY_ENTRY = ImmutableEntityEntry.of(EntityStatus.READ_ONLY);
    private static final EntityEntry DELETED_ENTITY_ENTRY = ImmutableEntityEntry.of(EntityStatus.DELETED);
    private static final EntityEntry GONE_ENTITY_ENTRY = ImmutableEntityEntry.of(EntityStatus.GONE);

    private final Map<Object, EntityEntry> contextEntityEntryMap;

    public EntityEntryContext() {
        contextEntityEntryMap = new HashMap<>();
    }

    public EntityEntry loading(Object object) {
        return contextEntityEntryMap.put(object, LOADING_ENTITY_ENTRY);
    }

    public EntityEntry saving(Object object) {
        return contextEntityEntryMap.put(object, SAVING_ENTITY_ENTRY);
    }

    public EntityEntry managed(Object object) {
        return contextEntityEntryMap.put(object, MANAGED_ENTITY_ENTRY);
    }

    public EntityEntry readOnly(Object object) {
        return contextEntityEntryMap.put(object, READ_ONLY_ENTITY_ENTRY);
    }

    public EntityEntry deleted(Object object) {
        return contextEntityEntryMap.put(object, DELETED_ENTITY_ENTRY);
    }

    public EntityEntry gone(Object object) {
        return contextEntityEntryMap.put(object, GONE_ENTITY_ENTRY);
    }

    public EntityEntry getEntityEntry(Object object) {
        return contextEntityEntryMap.get(object);
    }

    public void clear() {
        contextEntityEntryMap.clear();
    }
}
