package persistence.entity;

public class EntityEntry {
	private EntityStatus entityStatus;

	private EntityEntry(EntityStatus entityStatus) {
		this.entityStatus = entityStatus;
	}

	public static EntityEntry loadingOf() {
		return new EntityEntry(EntityStatus.LOADING);
	}

	public void manage() {
		this.entityStatus = EntityStatus.MANAGED;
	}

	public void readOnly() {
		this.entityStatus = EntityStatus.READ_ONLY;
	}

	public void delete() {
		this.entityStatus = EntityStatus.DELETED;
	}

	public void gone() {
		this.entityStatus = EntityStatus.GONE;
	}

	public void save() {
		this.entityStatus = EntityStatus.SAVING;
	}

	public boolean isReadOnly() {
		return entityStatus == EntityStatus.READ_ONLY;
	}

	public boolean isGone() {
		return entityStatus == EntityStatus.GONE;
	}

	public EntityStatus getStatus() {
		return entityStatus;
	}
}
