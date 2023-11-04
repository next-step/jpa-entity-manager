package persistence.entity;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimplePersistenceContext implements PersistenceContext {
	private final Map<EntityKey, Object> firstLevelCache = new HashMap<>();

	private final Map<EntityKey, Snapshot> snapshots = new HashMap<>();

	private final Map<EntityKey, EntityEntry> entityEntries = new HashMap<>();

	public SimplePersistenceContext() {
	}

	@Override
	public <T> T getEntity(Class<T> clazz, Object id) {
		EntityKey key = new EntityKey(clazz, id);
		EntityEntry entry = entityEntries.get(key);

		if(Objects.isNull(entry)) {
			return null;
		}

		if(entry.isGone()) {
			throw new EntityNotFoundException("영속성 컨텍스에서 삭제된 엔티티입니다.");
		}

		return clazz.cast(firstLevelCache.get(key));
	}

	@Override
	public void addEntity(Object id, Object entity) {
		EntityKey key = new EntityKey(entity.getClass(), id);
		EntityEntry entry = saveEntry(key);

		firstLevelCache.put(key, entity);
		snapshots.put(key, new Snapshot(entity));

		entry.manage();
	}

	@Override
	public void removeEntity(Object id, Object entity) {
		EntityKey key = new EntityKey(entity.getClass(), id);
		EntityEntry entry = entityEntries.get(key);

		if(Objects.isNull(entry)) {
			throw new EntityNotFoundException("영속성 컨텍스에 존재하지 않는 엔티티입니다.");
		}

		if(entry.isReadOnly()) {
			throw new IllegalArgumentException("읽기 전용 엔티티입니다.");
		}

		entry.delete();
		firstLevelCache.remove(key);
		snapshots.remove(key);

		entry.gone();
	}

	@Override
	public Snapshot getDatabaseSnapshot(Object id, Object entity) {
		return snapshots.put(new EntityKey(entity.getClass(), id), new Snapshot(entity));
	}

	@Override
	public Snapshot getCachedDatabaseSnapshot(Object id, Object entity) {
		return snapshots.get(new EntityKey(entity.getClass(), id));
	}

	private EntityEntry saveEntry(EntityKey key) {
		EntityEntry entry = entityEntries.get(key);

		if(Objects.isNull(entry)) {
			entry = new EntityEntry();
			entityEntries.put(key, entry);

			return entry;
		}

		entry.save();
		return entry;
	}

	public EntityStatus getStatus(Class<?> clazz, Object id) {
		return entityEntries.get(new EntityKey(clazz, id)).getStatus();
	}
}
