package persistence.entity;

public class EntityKey {
	private final Class<?> clazz;

	private final Long id;

	public EntityKey(Class<?> clazz, Long id) {
		this.clazz = clazz;
		this.id = id;
	}
}
