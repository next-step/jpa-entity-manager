package persistence.exception;

public class SnapshotCreationFailedException extends RuntimeException {
    public SnapshotCreationFailedException(Throwable cause) {
        super(cause);
    }
}
