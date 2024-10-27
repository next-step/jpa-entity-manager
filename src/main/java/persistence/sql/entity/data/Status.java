package persistence.sql.entity.data;

import java.util.List;

public enum Status {
    MANAGED,
    DELETED,
    SAVING,
    LOADING,
    GONE;

    private static final List<Status> MANAGED_STATUS = List.of(MANAGED, DELETED, SAVING);

    public static boolean isManaged(Status status) {
        return MANAGED_STATUS.contains(status);
    }
}
