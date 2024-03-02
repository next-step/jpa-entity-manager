package persistence.entity.context;

public interface PersistenceContext {

    // TODO: 요구사항과는 다르지만 일단 clazz 를 받습니다.
    Object getEntity(Class<?> clazz, Long id);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    boolean isRemoved(Object entity);
}
