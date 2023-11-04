package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityEntry {

    private Map<Integer, EntityStatus> entry;

    EntityEntry() {
        entry = new HashMap<>();
    }

    public boolean isManaged(Integer id) {
        if (!containsEntry(id)) {
            return false;
        }
        return entry.get(id).isManaged();
    }

    public boolean isGone(Integer id) {
        if (!containsEntry(id)) {
            return false;
        }
        return entry.get(id).isGone();
    }

    private boolean containsEntry(Integer id) {
        return entry.containsKey(id);
    }

    public void updateStatus(Integer id, EntityStatus entityStatus) {
        entry.put(id, entityStatus);
    }
}
