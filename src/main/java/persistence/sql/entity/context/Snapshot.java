package persistence.sql.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Snapshot {

    private final Map<EntityKey, Object> snapshot;

    public Snapshot() {
        this.snapshot = new ConcurrentHashMap<>();
    }

    public Object get(final EntityKey entityKey) {
        return snapshot.get(entityKey);
    }

    public void put(final EntityKey entityKey, final Object value) {
        snapshot.put(entityKey, value);
    }

    public void remove(final EntityKey entityKey) {
        snapshot.remove(entityKey);
    }

    public void clear() {
        snapshot.clear();
    }

}
