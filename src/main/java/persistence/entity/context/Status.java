package persistence.entity.context;

public enum Status {
    NONE,
    MANAGED,
    READ_ONLY,
    DELETED,
    GONE,
    LOADING,
    SAVING
}
