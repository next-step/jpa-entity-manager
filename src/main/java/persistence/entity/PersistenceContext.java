package persistence.entity;

public interface PersistenceContext {

    // TODO: 요구사항과는 다르지만 일단 entityClass 를 받습니다.
    Object getEntity(Class<?> entityClass, Long id);

    void addEntity(Object entity);

    void removeEntity(Object entity);
}
