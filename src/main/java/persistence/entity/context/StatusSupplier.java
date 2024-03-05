package persistence.entity.context;

import java.util.function.Supplier;

public class StatusSupplier {
    public static final Supplier<Boolean> NO = () -> false;
    public static final Supplier<Boolean> YES = () -> true;
    public static final Supplier<Boolean> NOT_INITIALIZED = () -> {
        throw new NotInitializedStatusException();
    };
    public static final Supplier<Boolean> OBJECT_NOT_FOUND = () -> {
        throw new ObjectNotFoundException();
    };
    public static final Supplier<Boolean> UNSUPPORTED = () -> {
        throw new UnsupportedOperationException();
    };
}
