package persistence.exception;


/**
 *  영속성 컨텍스트에 삭제된 후에 해당 엔터티를 다시 로드하려고 시도하면
 *  발생합니다.
 * */
public class ObjectNotFoundException extends RuntimeException {
    private final static String DEFAULT_MESSAGE = "이미 영속성 컨텍스트에 삭제된 엔터티입니다.";
    public ObjectNotFoundException() {
        super(DEFAULT_MESSAGE);
    }
}
