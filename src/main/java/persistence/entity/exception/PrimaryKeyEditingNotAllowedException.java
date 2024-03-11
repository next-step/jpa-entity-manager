package persistence.entity.exception;

public class PrimaryKeyEditingNotAllowedException extends IllegalArgumentException{
    public PrimaryKeyEditingNotAllowedException() {
        super("entity 식별자(primary key)를 변경할수 없습니다.");
    }
}
