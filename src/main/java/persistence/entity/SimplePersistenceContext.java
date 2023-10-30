package persistence.entity;

import persistence.sql.metadata.EntityMetadata;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext{
	private final EntityLoader entityLoader;

	private final Map<Map<Class<?>, Long>, Object> firstLevelCache = new HashMap<>();

	public SimplePersistenceContext(EntityLoader entityLoader) {
		this.entityLoader = entityLoader;
	}

	@Override
	public <T> T getEntity(Class<T> clazz, Long id) {
		Map<Class<?>, Long> key = Map.of(clazz, id);
		Object entity = firstLevelCache.get(key);

 		return clazz.cast(entity);
	}

	@Override
	public void addEntity(Long id, Object entity) {
		firstLevelCache.put(Map.of(entity.getClass(), id), entity);
	}

	@Override
	public void removeEntity(Long id, Object entity) {
		firstLevelCache.remove(Map.of(entity.getClass(), id));
	}
}
