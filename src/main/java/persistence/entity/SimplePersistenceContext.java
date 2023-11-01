package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {
	private final Map<EntityKey, Object> firstLevelCache = new HashMap<>();

	public SimplePersistenceContext() {
	}

	@Override
	public <T> T getEntity(Class<T> clazz, Long id) {
		return clazz.cast(firstLevelCache.get(new EntityKey(clazz, id)));
	}

	@Override
	public void addEntity(Long id, Object entity) {
		EntityKey key = new EntityKey(entity.getClass(), id);
		firstLevelCache.put(key, entity);
	}

	@Override
	public void removeEntity(Long id, Object entity) {
		EntityKey key = new EntityKey(entity.getClass(), id);
		firstLevelCache.remove(key);
	}
}
