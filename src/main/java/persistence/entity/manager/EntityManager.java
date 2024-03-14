package persistence.entity.manager;

import java.util.Optional;

public interface EntityManager {
    /**
     * 조회
     * @param clazz 클래스 타입
     * @param id 식별자
     * @return 조회 결과
     */
    <T> Optional<T> find(Class<T> clazz, Long id);

    /**
     * 저장
     * @param entity 저장 대상
     * @return 저장 결과
     */
    <T> T persist(T entity);

    <T> T merge(T entity);

    /**
     * 제거
     * @param entity 삭제 대상
     */
    void remove(Object entity);
}
