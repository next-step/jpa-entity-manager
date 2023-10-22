package persistence.exception;

/**
 * @Entity 어노테이션이 맵핑되어 있지 않는
 * 상황에서 발생되는 예외가 발생 합니다.
 *
 * */
public class NoEntityException extends IllegalArgumentException {
    private final static String DUALIST_MESSAGE = "엔티티 클래스가 아닙니다.";
    public NoEntityException() {
        super(DUALIST_MESSAGE);
    }
}
