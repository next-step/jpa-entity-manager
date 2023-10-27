package persistence.exception;

public class EntityManagerInitException extends RuntimeException {

    public EntityManagerInitException(Exception e) {
        super("엔티티 매니저 초기화 중 오류가 발생하였습니다. : {}", e);
    }

    public EntityManagerInitException(String message) {
        super(message);
    }
}
