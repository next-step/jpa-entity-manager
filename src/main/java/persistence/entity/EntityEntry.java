package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityEntry {

    private Map<Integer, EntityStatus> entry;

    EntityEntry() {
        entry = new HashMap<>();
    }

    public boolean isManaged(Integer hashCode) {
        if (!containsEntry(hashCode)) {
            return false;
        }
        return entry.get(hashCode).isManaged();
    }

    public boolean isSaving(Integer hashCode) {
        if (!containsEntry(hashCode)) {
            return false;
        }
        return entry.get(hashCode).isSaving();
    }

    public boolean isDeleted(Integer hashCode) {
        if (!containsEntry(hashCode)) {
            return false;
        }
        return entry.get(hashCode).isDeleted();
    }

    public boolean isGone(Integer hashCode) {
        if (!containsEntry(hashCode)) {
            return false;
        }
        return entry.get(hashCode).isGone();
    }

    public Map<Integer, EntityStatus> getEntry() {
        return entry;
    }

    public void managed(Integer hashCode) {
        entry.put(hashCode, EntityStatus.MANAGED);
    }

    public void loading(Integer hashCode) {
        entry.put(hashCode, EntityStatus.LOADING);
    }

    public void saving(Integer hashCode) {
        entry.put(hashCode, EntityStatus.SAVING);
    }

    public void deleted(Integer hashCode) {
        entry.put(hashCode, EntityStatus.DELETED);
    }

    public void gone(Integer hashCode) {
        entry.put(hashCode, EntityStatus.GONE);
    }

    public void clear(Integer hashCode) {
        entry.remove(hashCode);
    }

    public void clear() {
        entry.clear();
    }

    private boolean containsEntry(Integer hashCode) {
        return entry.containsKey(hashCode);
    }
}
