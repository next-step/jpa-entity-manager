package persistence.exception;

public class NoRowSnapshotException extends IllegalStateException {
    private static final String MESSAGE = "persistence context reported no row snapshot";

    public NoRowSnapshotException() {
        super(MESSAGE);
    }
}
