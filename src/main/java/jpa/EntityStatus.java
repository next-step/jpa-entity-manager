package jpa;

public enum EntityStatus {
    MERGED,
    READ_ONLY,
    DELETED,
    GONE,
    LOADING,
    SAVING,
    MANAGED;
}
