package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class EntityEntry {
    private final Map<EntityKey, EntityStatus> state;

    public EntityEntry() {
        this.state = new HashMap<>();
    }

    public void updateState(EntityKey entityKey, EntityStatus entityStatus) {
        state.put(entityKey, entityStatus);
    }
}
