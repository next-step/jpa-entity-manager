package persistence.exception;

import java.sql.SQLException;

public class NotFoundResultException extends SQLException {

    public NotFoundResultException() {
        super("결과값이 없습니다.");
    }

    public NotFoundResultException(String message) {
        super(message);
    }
}
