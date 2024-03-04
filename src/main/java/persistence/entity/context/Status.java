package persistence.entity.context;

import static persistence.entity.context.StatusSupplier.*;

public enum Status {
    INITIALIZING(NOT_INITIALIZED, NOT_INITIALIZED, NOT_INITIALIZED, NOT_INITIALIZED),

    NONE(NO, YES, NO, NO),
    MANAGED(YES, YES, YES, NO),
    READ_ONLY(YES, UNSUPPORTED, NO, NO),
    DELETED(OBJECT_NOT_FOUND, OBJECT_NOT_FOUND, NO, YES),
    GONE(OBJECT_NOT_FOUND, OBJECT_NOT_FOUND, NO, YES),
    LOADING(UNSUPPORTED, YES, NO, NO),
    SAVING(UNSUPPORTED, NO, NO, NO);

    private final StatusSupplier getPermission;
    private final StatusSupplier addPermission;
    private final StatusSupplier deletePermission;
    private final StatusSupplier alreadyRemoved;

    Status(StatusSupplier getPermission,
           StatusSupplier addPermission,
           StatusSupplier deletePermission,
           StatusSupplier alreadyRemoved) {
        this.getPermission = getPermission;
        this.addPermission = addPermission;
        this.deletePermission = deletePermission;
        this.alreadyRemoved = alreadyRemoved;
    }

    public boolean canGet() {
        return getPermission.check();
    }

    public boolean canAdd() {
        return addPermission.check();
    }

    public boolean canDelete() {
        return deletePermission.check();
    }

    public boolean isAlreadyRemoved() {
        return alreadyRemoved.check();
    }

}
