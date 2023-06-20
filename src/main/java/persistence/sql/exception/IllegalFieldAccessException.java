package persistence.sql.exception;

public class IllegalFieldAccessException extends RuntimeException {

    public IllegalFieldAccessException(IllegalAccessException e) {
        super(e);
    }
}
