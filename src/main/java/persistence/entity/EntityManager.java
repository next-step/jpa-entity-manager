package persistence.entity;

/**
 * + * EntityManager 는 데이터베이스와의 세션을 관리하고, 엔티티의 영속성 컨텍스트(Persistence Context)를 관리합니다.
 * + * 영속성 컨텍스트는 엔티티 객체의 변경을 추적하고, 데이터베이스와의 동기화를 담당합니다.
 * +
 */
public interface EntityManager {

    <T> T find(Object entity, Class<T> clazz, Long id);

    <T> T persist(T entity);

    boolean update(Object entity);

    void remove(Object entity);

    <T> T merge(T entity);
}
