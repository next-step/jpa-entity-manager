package persistence.entity.impl.listener;

import persistence.entity.Event;
import persistence.entity.EventListener;
import persistence.entity.EventSource;
import persistence.entity.impl.store.EntityPersister;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityObjectMappingMeta;

public class PersistEventListenerImpl implements EventListener {

    private final EntityPersister entityPersister;
    private final ColumnType columnType;

    public PersistEventListenerImpl(EntityPersister entityPersister, ColumnType columnType) {
        this.entityPersister = entityPersister;
        this.columnType = columnType;
    }

    @Override
    public void onEvent(Event event) {
        throw new RuntimeException("PersistEventListener는 반환값이 있는 이벤트만 처리할 수 있습니다.");
    }

    @Override
    public <T> T onEvent(Class<T> clazz, Event event) {
        final Object savedEntity = entityPersister.store(event.getEntity(), columnType);
        final EventSource eventSource = event.getEventSource();
        eventSource.saving(savedEntity);

        syncPersistenceContext(eventSource, savedEntity);
        return clazz.cast(savedEntity);
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        final EntityObjectMappingMeta savedObjectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        eventSource.putEntity(savedObjectMappingMeta.getIdValue(), entity);
    }
}
