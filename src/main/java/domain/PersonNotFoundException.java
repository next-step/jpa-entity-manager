package domain;

public class PersonNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Person 엔티티를 조회하는데 실패했습니다.";

    public PersonNotFoundException() {
        super(MESSAGE);
    }
}
