package persistence.defaulthibernate;

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
    private final Map<Class<?>, Map<Long, Object>> entitiesByKey = new HashMap<>();
    private final Map<Class<?>, Map<Long, Object>> entitySnapshotsByKey = new HashMap<>();
    private final Map<Class<?>, Map<Object, EntityEntry>> entityEntry = new HashMap<>();

    @Override
    public void add(Object object, Long id) {
        Class<?> clazz = object.getClass();
        entitiesByKey.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>())
                .put(id, object);
        entitySnapshotsByKey.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>()).put(id, object);
        entityEntry.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>()).put(object, EntityEntry.status(EntryStatus.MANAGED));
    }

    @Override
    public Object get(Class<?> clazz, Long id) {
        Map<Long, Object> entityMap = entitiesByKey.get(clazz);
        if (entityMap == null || !entityMap.containsKey(id)) {
            throw new IllegalArgumentException("Entity not found");
        }
        return entityMap.get(id);
    }

    @Override
    public void update(Object object, Long id) {
        Class<?> clazz = object.getClass();
        entitiesByKey.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>())
                .put(id, object);
    }

    @Override
    public void remove(Class<?> clazz, Long id) {
        Map<Long, Object> entityMap = entitiesByKey.get(clazz);
        if (entityMap == null || !entityMap.containsKey(id)) {
            throw new IllegalArgumentException("Entity not found");
        }
        entityEntry.get(clazz).get(entityMap.get(id)).updateStatus(EntryStatus.DELETED);
        entityMap.remove(id);
        removeSnapshots(clazz, id);
    }

    public List<Object> getDirtyObjects() {
        return entitySnapshotsByKey.entrySet().stream()
                .flatMap(entry -> entry.getValue().entrySet().stream())
                .filter(entry -> isDirty(entry.getValue(), entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();
    }

    public void clearSnapshots() {
        entitySnapshotsByKey.clear();
    }

    public void setEntityEntryStatus(Object o, EntryStatus status) {
        entityEntry.get(o.getClass()).get(o).updateStatus(status);
    }

    private boolean isDirty(Object object, Long id) {
        Class<?> clazz = object.getClass();
        boolean equals = clazz.equals(entitiesByKey.get(clazz).get(id));
        boolean isManaged = entityEntry.get(clazz).get(object).status() == EntryStatus.MANAGED
                || entityEntry.get(clazz).get(object).status() == EntryStatus.DELETED;
        return !equals && isManaged;
    }

    private void removeSnapshots(Class<?> clazz, Long id) {
        entitySnapshotsByKey.computeIfPresent(clazz, (k, v) -> {
            v.remove(id);
            return v;
        });
    }

    public boolean isExist(Class<?> clazz, Long id) {
        Map<Long, Object> entityMap = entitiesByKey.get(clazz);
        return entityMap != null && entityMap.containsKey(id);
    }
}
