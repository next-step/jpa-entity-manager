package persistence.entity.database;

/**
 * persist() 실행 시 id 가 누락된 경우 던져지는 예외입니다.<p>
 * <p>
 * JPA entity 정의에서 @Id 와 함께 @GeneratedValue 가 정의되지 않았다면, persist() 에 전달되는 객체에 id 가 필수로 선언되어 있어야 합니다.
 * 이 예외가 발생하면 JPA entity 의 @Id 필드 정의와 entity 객체의 상태를 확인하세요.
 */
public class PrimaryKeyMissingException extends RuntimeException {
    public PrimaryKeyMissingException(String entityClassName) {
        super("Primary key is not assigned when inserting: " + entityClassName);
    }
}
