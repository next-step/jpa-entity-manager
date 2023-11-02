package persistence.entity;

import persistence.sql.metadata.EntityMetadata;

public class CustomJpaRepository <T> {
	private final EntityManager entityManager;

	public CustomJpaRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public T save(T t) {
		if(new EntityMetadata(t).isNewEntity()) {
			entityManager.persist(t);
			return t;
		}

		entityManager.merge(t);
		return t;
	}
}
