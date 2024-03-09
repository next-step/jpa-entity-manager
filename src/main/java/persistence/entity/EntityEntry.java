package persistence.entity;

public class EntityEntry {

    private Status status;

    public EntityEntry(Status status) {
        this.status = status;
    }

    public void managed() {
        this.status = Status.MANAGED;
    }

    public void readOnly() {
        this.status = Status.READ_ONLY;
    }

    public void deleted() {
        this.status = Status.DELETED;
    }

    public void gone() {
        this.status = Status.GONE;
    }

    public void loading() {
        this.status = Status.LOADING;
    }

    public void saving() {
        this.status = Status.SAVING;
    }

    public Status status() {
        return status;
    }
}
