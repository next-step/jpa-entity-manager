package persistence.entity.context;

import java.util.function.Supplier;

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

    private final Supplier<Boolean> getPermission;
    private final Supplier<Boolean> addPermission;
    private final Supplier<Boolean> deletePermission;
    private final Supplier<Boolean> alreadyRemoved;

    Status(Supplier<Boolean> getPermission,
           Supplier<Boolean> addPermission,
           Supplier<Boolean> deletePermission,
           Supplier<Boolean> alreadyRemoved) {
        this.getPermission = getPermission;
        this.addPermission = addPermission;
        this.deletePermission = deletePermission;
        this.alreadyRemoved = alreadyRemoved;
    }

    public boolean canGet() {
        return getPermission.get();
    }

    public boolean canAdd() {
        return addPermission.get();
    }

    public boolean canDelete() {
        return deletePermission.get();
    }

    public boolean isAlreadyRemoved() {
        return alreadyRemoved.get();
    }
}
