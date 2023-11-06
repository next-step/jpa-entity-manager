package persistence.entity.impl.publisher;

import persistence.entity.EntityEventPublisher;
import persistence.entity.EventListener;
import persistence.entity.impl.event.DeleteEvent;
import persistence.entity.impl.event.LoadEvent;
import persistence.entity.impl.event.MergeEvent;
import persistence.entity.impl.event.PersistEvent;

public class EntityEventPublisherImpl implements EntityEventPublisher {

    private final EventListener loadEventListener;
    private final EventListener mergeEventListener;
    private final EventListener persistEventListener;
    private final EventListener deleteEventListener;

    public EntityEventPublisherImpl(EventListener loadEventListener, EventListener mergeEventListener, EventListener persistEventListener,
        EventListener deleteEventListener) {
        this.loadEventListener = loadEventListener;
        this.mergeEventListener = mergeEventListener;
        this.persistEventListener = persistEventListener;
        this.deleteEventListener = deleteEventListener;
    }

    @Override
    public Object onLoad(LoadEvent event) {
        return loadEventListener.onEvent(event.getClazz(), event);
    }

    @Override
    public Object onPersist(PersistEvent event) {
        return persistEventListener.onEvent(event.getClazz(), event);
    }

    @Override
    public Object onMerge(MergeEvent event) {
        return mergeEventListener.onEvent(event.getClazz(), event);
    }

    @Override
    public void onDelete(DeleteEvent event) {
        deleteEventListener.onEvent(event);
    }
}
