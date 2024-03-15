package persistence.entity.exception;

public class OptionalForbiddenException extends IllegalArgumentException{
    public OptionalForbiddenException() {
        super("Optional은 허용되지 않습니다.");
    }
}
