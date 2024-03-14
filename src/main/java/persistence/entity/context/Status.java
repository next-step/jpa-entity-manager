package persistence.entity.context;

public enum Status {
    MANAGED,
    READ_ONLY,
    DELETED,
    GONE,
    LOADING,
    SAVING;
}
