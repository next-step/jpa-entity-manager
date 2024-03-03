package persistence.sql.dml.exception;

public class InvalidUpdateSqlException extends RuntimeException{

    private static final String MESSAGE = "업데이트 쿼리 실행중 에러가 발생하였습니다.";

    public InvalidUpdateSqlException() {
        super(MESSAGE);
    }
}
