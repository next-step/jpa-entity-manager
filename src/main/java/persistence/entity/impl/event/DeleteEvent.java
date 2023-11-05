package persistence.entity.impl.event;

import persistence.entity.Event;
import persistence.entity.EventSource;

public class DeleteEvent implements Event {

    private final Class<?> clazz;
    private final Object entity;
    private final EventSource eventSource;

    private DeleteEvent(Class<?> clazz, Object entity, EventSource eventSource) {
        this.clazz = clazz;
        this.entity = entity;
        this.eventSource = eventSource;
    }

    public static DeleteEvent of(Object entity, EventSource eventSource) {
        return new DeleteEvent(entity.getClass(), entity, eventSource);
    }

    @Override
    public EventSource getEventSource() {
        return eventSource;
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
