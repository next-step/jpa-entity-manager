package persistence.entity.impl.event;

import persistence.entity.Event;
import persistence.entity.EventSource;

public class LoadEvent implements Event {

    private final Object id;
    private final Class<?> clazz;
    private final EventSource eventSource;

    private LoadEvent(EventSource eventSource, Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.eventSource = eventSource;
        this.id = id;
    }

    public static LoadEvent of(Class<?> clazz, Object id, EventSource eventSource) {
        return new LoadEvent(eventSource, clazz, id);
    }

    @Override
    public EventSource getEventSource() {
        return eventSource;
    }

    @Override
    public Object getEntity() {
        return null;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getId() {
        return id;
    }
}
