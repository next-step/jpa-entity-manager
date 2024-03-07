package persistence.entity;

public interface EntityManager<T> {
    /**
     * 조회
     * @param clazz 클래스 타입
     * @param id 식별자
     * @return 조회 결과
     */
    T find(Class<T> clazz, Long id);

    /**
     * 저장
     * @param entity 저장 대상
     * @return 저장 결과
     */
    T persist(T entity);

    /**
     * 업데이트
     * @param entity 업데이트 대상
     * @return 업데이트 결과
     */
    boolean update(T entity);

    /**
     * 제거
     * @param entity 삭제 대상
     */
    void remove(T entity);
}
