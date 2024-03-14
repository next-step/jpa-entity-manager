package persistence.entity.exception;

import java.util.Optional;

public class OptionalForbiddenException extends IllegalArgumentException{
    public OptionalForbiddenException() {
        super("Optional은 허용되지 않습니다.");
    }

    public <T> void validate(T entity) {
        if (entity instanceof Optional) {
            throw new OptionalForbiddenException();
        }
    }
}
