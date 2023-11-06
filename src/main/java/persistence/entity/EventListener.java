package persistence.entity;

public interface EventListener {

    void onEvent(Event event);

    <T> T onEvent(Class<T> clazz, Event event);

    void syncPersistenceContext(EventSource eventSource, Object entity);
}
