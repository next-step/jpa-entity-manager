package persistence.entity;

import java.io.Serializable;

public class EntityEntry {
    private Status status;
    private Serializable id;

    private EntityEntry(Status status, Serializable id) {
        this.status = status;
        this.id = id;
    }

    public static EntityEntry inSaving() {
        return new EntityEntry(Status.SAVING, 0L);
    }

    public static EntityEntry deleted(Serializable id) {
        return new EntityEntry(Status.DELETED, id);
    }

    public static EntityEntry managed(Serializable id) {
        return new EntityEntry(Status.MANAGED, id);
    }

    public static EntityEntry loading(Serializable id) {
        return new EntityEntry(Status.LOADING, id);
    }

    public boolean isManaged() {
        return status.isManaged();
    }

    public boolean isNotSaving() {
        return status != Status.SAVING;
    }

    public void updateStatus(Status status) {
        if (this.status.isValidStatusTransition(status)) {
            this.status = status;
            return;
        }

        throw new IllegalArgumentException("Invalid status transition from: " + this.status + " to: " + status);
    }

    public Serializable getId() {
        return id;
    }

    public boolean isNotReadable() {
        return status == Status.DELETED || status == Status.GONE;
    }
}
