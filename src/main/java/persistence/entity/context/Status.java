package persistence.entity.context;

import java.util.function.Supplier;

import static persistence.entity.context.StatusSupplier.*;

public enum Status {
    NONE(NO, YES, NO, NO),
    MANAGED(YES, YES, YES, NO),
    READ_ONLY(YES, UNSUPPORTED, NO, NO),
    DELETED(OBJECT_NOT_FOUND, OBJECT_NOT_FOUND, NO, YES),
    GONE(OBJECT_NOT_FOUND, OBJECT_NOT_FOUND, NO, YES),
    LOADING(UNSUPPORTED, YES, NO, NO),
    SAVING(UNSUPPORTED, NO, NO, NO);

    private final Supplier<Boolean> readable;
    private final Supplier<Boolean> assignable;
    private final Supplier<Boolean> removable;
    private final Supplier<Boolean> alreadyRemoved;

    Status(Supplier<Boolean> readable,
           Supplier<Boolean> assignable,
           Supplier<Boolean> removable,
           Supplier<Boolean> alreadyRemoved) {
        this.readable = readable;
        this.assignable = assignable;
        this.removable = removable;
        this.alreadyRemoved = alreadyRemoved;
    }

    public boolean isReadable() {
        return readable.get();
    }

    public boolean isAssignable() {
        return assignable.get();
    }

    public boolean isRemovable() {
        return removable.get();
    }

    public boolean isAlreadyRemoved() {
        return alreadyRemoved.get();
    }
}
