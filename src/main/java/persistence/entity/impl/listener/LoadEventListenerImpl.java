package persistence.entity.impl.listener;

import persistence.entity.Event;
import persistence.entity.EventListener;
import persistence.entity.EventSource;
import persistence.entity.impl.retrieve.EntityLoader;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityObjectMappingMeta;

public class LoadEventListenerImpl implements EventListener {

    private final EntityLoader entityLoader;
    private final ColumnType columnType;

    public LoadEventListenerImpl(EntityLoader entityLoader, ColumnType columnType) {
        this.entityLoader = entityLoader;
        this.columnType = columnType;
    }

    @Override
    public void onEvent(Event event) {
        throw new RuntimeException("LoadEvent는 반환값이 항상 존재합니다.");
    }

    @Override
    public <T> T onEvent(Class<T> clazz, Event event) {
        final T loadedEntity = entityLoader.load(clazz, event.getId(), columnType);
        final EventSource eventSource = event.getEventSource();
        eventSource.loading(loadedEntity);

        syncPersistenceContext(eventSource, loadedEntity);
        return loadedEntity;
    }

    @Override
    public void syncPersistenceContext(EventSource eventSource, Object entity) {
        final EntityObjectMappingMeta savedObjectMappingMeta = EntityObjectMappingMeta.of(entity, columnType);

        eventSource.putEntity(savedObjectMappingMeta.getIdValue(), entity);
    }
}
