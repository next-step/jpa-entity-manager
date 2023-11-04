package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityEntry {

    private Map<Integer, EntityStatus> entry;

    EntityEntry() {
        entry = new HashMap<>();
    }

    public boolean isManaged(Integer key) {
        if (!containsEntry(key)) {
            return false;
        }
        return entry.get(key).isManaged();
    }

    public boolean isGone(Integer key) {
        if (!containsEntry(key)) {
            return false;
        }
        return entry.get(key).isGone();
    }

    public void managed(Integer key) {
        entry.put(key, EntityStatus.MANAGED);
    }

    public void loading(Integer key) {
        entry.put(key, EntityStatus.LOADING);
    }

    public void saving(Integer key) {
        entry.put(key, EntityStatus.SAVING);
    }

    public void deleted(Integer key) {
        entry.put(key, EntityStatus.DELETED);
    }

    public void gone(Integer key) {
        entry.put(key, EntityStatus.GONE);
    }

    private boolean containsEntry(Integer key) {
        return entry.containsKey(key);
    }
}
