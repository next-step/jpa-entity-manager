package persistence.entity;

public enum EntityStatus {

    NEW,
    MANAGED,
    READ_ONLY,
    DELETED,
    GONE,
    LOADING,
    SAVING;

    public boolean isGone() {
        return this == GONE;
    }
    public boolean isReadOnly() {
        return this == READ_ONLY;
    }

    public boolean isManaged() {
        return this == MANAGED;
    }

    public boolean isLoading() {
        return this == LOADING;
    }

    public boolean isDeleted() {
        return this == DELETED;
    }
}
