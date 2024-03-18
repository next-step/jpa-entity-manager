package persistence.entity.exception;

public class ReadOnlyException extends IllegalStateException{
    public ReadOnlyException() {
        super("읽기 전용 entity는 수정할 수 없습니다.");
    }
}
