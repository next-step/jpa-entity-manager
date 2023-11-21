package persistence.entity.entry;

public enum EntityStatus {
    MANAGED,
    READ_ONLY,
    DELETED,
    GONE,
    LOADING,
    SAVING,
    ;

    public static boolean updateAble(EntityStatus entityStatus) {
        return entityStatus == MANAGED;
    }

    public static boolean insertAble(EntityStatus entityStatus) {
        return entityStatus == null || entityStatus == MANAGED;
    }

    public static boolean readAble(EntityStatus entityStatus) {
        return entityStatus == MANAGED || entityStatus == READ_ONLY;
    }
}
