package persistence.entity.exception;

public class NotUniqueDataException extends IllegalArgumentException{
    public NotUniqueDataException(Long id) {
        super(String.format("2개 이상의 데이터가 조회되었습니다. 유니크한 데이터만 조회하실 수 있습니다. (조회한 id: %d)", id));
    }
}
