package persistence.sql.entity.context;

public class EntityEntry {

    private Object entity;
    private EntityStatus status;

    public EntityEntry(final Object entity,
                       final EntityStatus status) {
        this.entity = entity;
        this.status = status;
    }

    public boolean isEquals(final Object entity) {
        return this.entity.equals(entity);
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
