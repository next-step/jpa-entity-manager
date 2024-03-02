package persistence.jpa;

import persistence.entity.EntityKey;
import persistence.entity.EntityMetaData;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SnapShot {
    private final Map<EntityKey, EntityMetaData> snapshot;

    public SnapShot() {
        this.snapshot = new HashMap<>();
    }


    public void save(EntityKey entityKey, EntityMetaData entity) {
        snapshot.put(entityKey, entity);
    }

    public EntityMetaData get(EntityKey entityKey) {
        return snapshot.get(entityKey);
    }
}
