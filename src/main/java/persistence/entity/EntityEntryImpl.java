package persistence.entity;

public class EntityEntryImpl implements EntityEntry {
    private Status status;

    public EntityEntryImpl(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setSaving() {
        status = Status.SAVING;
    }

    @Override
    public void setManaged() {
        status = Status.MANAGED;
    }

    @Override
    public void setLoading() {
        status = Status.LOADING;
    }

    @Override
    public void setDeleted() {
        status = Status.DELETED;
    }

    @Override
    public void setGone() {
        status = Status.GONE;
    }

    @Override
    public void setReadOnly() {
        status = Status.READ_ONLY;
    }

    @Override
    public boolean isReadOnly() {
        return status == Status.READ_ONLY;
    }
}
