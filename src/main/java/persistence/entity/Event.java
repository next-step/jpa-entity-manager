package persistence.entity;

public interface Event {

    EventSource getEventSource();

    Object getEntity();

    Object getId();

}
