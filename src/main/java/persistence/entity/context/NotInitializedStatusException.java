package persistence.entity.context;

/**
 * persistence.entity.context.Status 가 아직 초기화되지 않았을 때 발생하는 예외입니다.<p>
 *
 * 초기화 된 이후에 바로 다른 상태로 전이될 수 있도록 작성되었는지 확인해주세요.
 */
public class NotInitializedStatusException extends RuntimeException {
}
