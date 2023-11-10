package persistence.entity.persistencecontext;

public enum EntityStatus {
    MANAGED,
//    READ_ONLY, // TODO 추후 구현 예정
    DELETED,
    GONE,
    LOADING,
    SAVING
}
