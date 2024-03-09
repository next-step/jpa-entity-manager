package persistence.sql.entity.context;

public class EntityEntry {

    private EntityStatus status;

    public EntityEntry(final EntityStatus status) {
        this.status = status;
    }

    public void readOnly() {
        this.status = EntityStatus.READ_ONLY;
    }

    public void deleted() {
        this.status = EntityStatus.DELETED;
    }

    public void gone() {
        this.status = EntityStatus.GONE;
    }

    public void loading() {
        this.status = EntityStatus.LOADING;
    }

    public void saving() {
        this.status = EntityStatus.SAVING;
    }

    public boolean isGone() {
        return this.status == EntityStatus.GONE;
    }

    public boolean isReadOnly() {
        return this.status == EntityStatus.READ_ONLY;
    }

}
