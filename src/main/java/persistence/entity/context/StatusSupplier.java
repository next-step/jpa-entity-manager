package persistence.entity.context;

import java.util.function.Supplier;

public class StatusSupplier {
    private final Supplier<Boolean> supplier;

    public static final StatusSupplier YES = create(() -> true);
    public static final StatusSupplier NO = create(() -> false);
    public static final StatusSupplier UNSUPPORTED = create(() -> {
        throw new UnsupportedOperationException();
    });
    public static final StatusSupplier OBJECT_NOT_FOUND = create(() -> {
        throw new ObjectNotFoundException();
    });
    public static final StatusSupplier NOT_INITIALIZED = create(() -> {
        throw new NotInitializedStatusException();
    });

    private StatusSupplier(Supplier<Boolean> checker) {
        this.supplier = checker;
    }

    private static StatusSupplier create(Supplier<Boolean> checker) {
        return new StatusSupplier(checker);
    }

    public boolean check() {
        return supplier.get();
    }
}
