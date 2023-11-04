package persistence.entity;

public class EntityEntry {
	private EntityStatus entityStatus;

	public EntityEntry() {
		this.entityStatus = EntityStatus.LOADING;
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
		if(entityStatus == EntityStatus.READ_ONLY) {
			return true;
		}

		return false;
	}

	public boolean isGone() {
		if(entityStatus == EntityStatus.GONE) {
			return true;
		}

		return false;
	}

	public EntityStatus getStatus() {
		return entityStatus;
	}
}
