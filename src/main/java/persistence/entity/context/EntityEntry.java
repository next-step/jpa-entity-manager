package persistence.entity.context;

public class EntityEntry {

    private Status status;

    public EntityEntry() {
        this.status = Status.MANAGED;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public boolean is(final Status status) {
        return this.status == status;
    }
}
