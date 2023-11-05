package persistence.entity.impl.event;

import persistence.entity.Event;
import persistence.entity.EventSource;

public class PersistEvent implements Event {

    private final Class<?> clazz;
    private final Object entity;
    private final EventSource eventSource;

    private PersistEvent(Class<?> clazz, Object entity, EventSource eventSource) {
        this.clazz = clazz;
        this.entity = entity;
        this.eventSource = eventSource;
    }

    public static PersistEvent of(Object entity, EventSource eventSource) {
        return new PersistEvent(entity.getClass(), entity, eventSource);
    }

    @Override
    public EventSource getEventSource() {
        return eventSource;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public Object getId() {
        throw new UnsupportedOperationException();
    }
}
