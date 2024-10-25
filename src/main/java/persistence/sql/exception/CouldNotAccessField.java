package persistence.sql.exception;

public class CouldNotAccessField extends RuntimeException {

    public CouldNotAccessField(Exception exception, ExceptionMessage exceptionMessage) {
        super(exceptionMessage.getMessage(), exception);
    }

}
