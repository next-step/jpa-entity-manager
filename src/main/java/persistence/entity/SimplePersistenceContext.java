package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {
	private final Map<EntityKey, Object> firstLevelCache = new HashMap<>();

	private final Map<EntityKey, Snapshot> snapshots = new HashMap<>();

	public SimplePersistenceContext() {
	}

	@Override
	public <T> T getEntity(Class<T> clazz, Object id) {
		return clazz.cast(firstLevelCache.get(new EntityKey(clazz, id)));
	}

	@Override
	public void addEntity(Object id, Object entity) {
		EntityKey key = new EntityKey(entity.getClass(), id);
		firstLevelCache.put(key, entity);
		snapshots.put(key, new Snapshot(entity));
	}

	@Override
	public void removeEntity(Object id, Object entity) {
		EntityKey key = new EntityKey(entity.getClass(), id);
		firstLevelCache.remove(key);
		snapshots.remove(key);
	}

	@Override
	public Snapshot getDatabaseSnapshot(Object id, Object entity) {
		return snapshots.put(new EntityKey(entity.getClass(), id), new Snapshot(entity));
	}

	@Override
	public Snapshot getCachedDatabaseSnapshot(Object id, Object entity) {
		EntityKey key = new EntityKey(entity.getClass(), id);
		return snapshots.get(key);
	}

}
