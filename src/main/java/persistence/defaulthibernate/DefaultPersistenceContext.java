package persistence.defaulthibernate;

import persistence.entity.EntityData;
import persistence.entity.EntityKey;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * entitySnapshotsByKey 필드를 추가하고, getDatabaseSnapShot 메서드를 구현하세요.
 * entituSnapshotsByKey는 엔티티의 스냅샷을 저장하는 맵입니다.
 * entitySnapshotsByKey 맵은 엔티티의 클래스와 id를 키로 하고, 엔티티의 스냅샷을 값으로 합니다.
 * entitySnapshotsByKey 맵에 저장되는 엔티티의 스냅샷은 getDatabaseSnapShot 메서드를 호출할 때, entitiesByKey 맵에서 엔티티를 가져와서 저장합니다.
 * add 할 때는, getDatabaseSnapShot 메서드를 호출해서 스냅샷을 저장합니다.
 * remove할때는 entitySnapshotsByKey 맵에서 해당 엔티티의 스냅샷을 삭제합니다.
 */

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, EntityData> entitiesByKey = new HashMap<>();
    private final Map<EntityKey, EntityData>  entitySnapshotsByKey = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entityEntry = new HashMap<>();

    @Override
    public void add(EntityData entityData, EntityKey entityKey) {

        entitiesByKey.computeIfAbsent(entityKey, k -> entityData);
        entitySnapshotsByKey.computeIfAbsent(entityKey, k -> entityData);
    }

    @Override
    public Object get(EntityKey entityKey) {
        Object o = entitiesByKey.get(entityKey);
        if (o == null) {
            throw new IllegalArgumentException("Entity not found");
        }
        return o;
    }

    @Override
    public void update(EntityData entityData, EntityKey entityKey) {
        entitiesByKey.computeIfAbsent(entityKey, k -> entityData);
    }

    @Override
    public void remove(EntityKey entityKey) {
        Object o = entitiesByKey.get(entityKey);
        if ( o == null ) {
            throw new IllegalArgumentException("Entity not found");
        }
        entitiesByKey.remove(entityKey);
        removeSnapshots(entityKey);
    }

    public List<Object> getDirtyObjects() {
        return entitySnapshotsByKey.entrySet().stream()
                .filter(e -> isDirty(new EntityKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

    public void clearSnapshots() {
        entitySnapshotsByKey.clear();
    }

    public void setEntityEntryStatus(EntityKey entityKey, EntryStatus status) {
        entityEntry.put(entityKey, new EntityEntry(status));
    }

    private boolean isDirty(Object object, Long id) {
        Class<?> clazz = object.getClass();
        boolean equals = entitySnapshotsByKey.entrySet().stream().allMatch(e -> {
            if (e.getKey().getId().equals(id) && e.getKey().getEntityClass().equals(clazz)) {
                return e.getValue().equals(object);
            }
            return false;
        });
        return !equals;
    }

    private void removeSnapshots(EntityKey entityKey) {
        entitySnapshotsByKey.remove(entityKey);
    }

    public boolean isExist(EntityKey entityKey) {
        Map<Long, Object> entityMap = entitiesByKey.get(clazz);
        return entityMap != null && entityMap.containsKey(id);
    }


}
