package persistence.entity;

import java.util.HashSet;
import java.util.Set;

public class PendingEntities {

    private final Set<Object> pendingEntities = new HashSet<>();

    public void persistEntity(Object entity) {
        pendingEntities.add(entity);
    }

    public Set<Object> getEntities() {
        return pendingEntities;
    }

    public void removeEntity(Object entity) {
        pendingEntities.remove(entity);
    }

    public void clear() {
        pendingEntities.clear();
    }

}
