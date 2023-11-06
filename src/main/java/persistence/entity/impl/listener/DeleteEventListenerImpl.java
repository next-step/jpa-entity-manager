package persistence.entity.impl.listener;

import persistence.entity.EntityEntry;
import persistence.entity.Event;
import persistence.entity.EventListener;
import persistence.entity.EventSource;
import persistence.entity.impl.store.EntityPersister;
import persistence.sql.dialect.ColumnType;

public class DeleteEventListenerImpl implements EventListener {

    private final EntityPersister entityPersister;
    private final ColumnType columnType;

    public DeleteEventListenerImpl(EntityPersister entityPersister, ColumnType columnType) {
        this.entityPersister = entityPersister;
        this.columnType = columnType;
    }

    @Override
    public void onEvent(Event event) {
        final Object entity = event.getEntity();
        final EventSource eventSource = event.getEventSource();

        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        if (entityEntry.isReadOnly()) {
            throw new RuntimeException("해당 Entity는 삭제될 수 없습니다.");
        }

        entityPersister.delete(entity, columnType);

        eventSource.deleted(entity);

        syncPersistenceContext(eventSource, entity);
    }

    @Override
    public <T> T onEvent(Class<T> clazz, Event event) {
        throw new RuntimeException("DeleteEventListener는 반환이 있는 이벤트를 지원하지 않습니다.");
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        eventSource.purgeEntity(entity);
    }
}
