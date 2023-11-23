package persistence.entity.entry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityEntry {
    private final Map<Integer, EntityStatus> entityStatusMap = new ConcurrentHashMap<>();

    public EntityEntry() {
    }

    public void updateLoading(Object object) {
        entityStatusMap.put(getEntryStatusMapKey(object), EntityStatus.LOADING);
    }

    public void updateManaged(Object object) {
        entityStatusMap.put(getEntryStatusMapKey(object), EntityStatus.MANAGED);
    }

    public void updateSaving(Object object) {
        entityStatusMap.put(getEntryStatusMapKey(object), EntityStatus.SAVING);
    }

    public void updateDeleted(Object object) {
        entityStatusMap.put(getEntryStatusMapKey(object), EntityStatus.DELETED);
    }

    public void updateGone(Object object) {
        entityStatusMap.put(getEntryStatusMapKey(object), EntityStatus.GONE);
    }

    public boolean readAble(Object object) {
        return EntityStatus.readAble(entityStatusMap.get(getEntryStatusMapKey(object)));
    }

    public boolean updateAble(Object object) {
        return EntityStatus.updateAble(entityStatusMap.get(getEntryStatusMapKey(object)));
    }

    public boolean insertAble(Object object) {
        return EntityStatus.insertAble(entityStatusMap.get(getEntryStatusMapKey(object)));
    }

    private int getEntryStatusMapKey(Object object) {
        if (object == null) {
            throw new NullPointerException("object should not be null to get key");
        }
        return System.identityHashCode(object);
    }
}
