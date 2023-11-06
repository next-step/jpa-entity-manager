package persistence.entity.impl.listener;

import persistence.entity.EntityEntry;
import persistence.entity.Event;
import persistence.entity.EventListener;
import persistence.entity.EventSource;
import persistence.entity.impl.store.EntityPersister;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityObjectMappingMeta;

public class MergeEventListenerImpl implements EventListener {

    private final EntityPersister entityPersister;
    private final ColumnType columnType;

    public MergeEventListenerImpl(EntityPersister entityPersister, ColumnType columnType) {
        this.entityPersister = entityPersister;
        this.columnType = columnType;
    }

    @Override
    public void onEvent(Event event) {
        throw new RuntimeException("MergeEvent는 반환이 없는 이벤트를 지원하지 않습니다.");
    }

    @Override
    public <T> T onEvent(Class<T> clazz, Event event) {
        final Object entity = event.getEntity();

        final EventSource eventSource = event.getEventSource();
        final EntityEntry entityEntry = eventSource.getEntityEntry(entity);
        if (entityEntry.isReadOnly()) {
            throw new RuntimeException("해당 Entity는 변경될 수 없습니다.");
        }

        entityPersister.update(entity, columnType);
        syncPersistenceContext(eventSource, entity);
        return clazz.cast(entity);
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        final EntityObjectMappingMeta savedObjectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        eventSource.putEntity(savedObjectMappingMeta.getIdValue(), entity);
    }
}
