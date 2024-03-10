package persistence.entity.domain;

public enum EntityStatus {
    MANAGED,
    READ_ONLY,
    DELETED,
    GONE,
    LOADING,
    SAVING
}
