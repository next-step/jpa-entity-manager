package persistence.entity.persister;

import java.util.concurrent.ConcurrentHashMap;

public class EntityPersisterConcurrentMap {

    private final ConcurrentHashMap<String, EntityPersister> map = new ConcurrentHashMap<>();

    public synchronized void put(final String name, final EntityPersister entityPersister) {
        map.putIfAbsent(name, entityPersister);
    }

    public EntityPersister get(final String name) {
        return map.get(name);
    }

}
