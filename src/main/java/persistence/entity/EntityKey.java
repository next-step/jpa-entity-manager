package persistence.entity;

public class EntityKey {
	private final Class<?> clazz;

	private final Object id;

	public EntityKey(Class<?> clazz, Object id) {
		this.clazz = clazz;
		this.id = id;
	}
}
