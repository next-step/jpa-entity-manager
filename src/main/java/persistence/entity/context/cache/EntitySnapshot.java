package persistence.entity.context.cache;

import persistence.model.EntityMetaData;
import persistence.model.EntityMetaDataMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EntitySnapshot {

    private final EntityMetaData entityMetaData;
    private final Map<String, Object> values = new HashMap<>();

    public EntitySnapshot(final Object entity) {
        this.entityMetaData = EntityMetaDataMapping.getMetaData(entity.getClass().getName());
        values.putAll(entityMetaData.extractValues(entity));
    }

    public boolean checkDirty(final Object entity) {
        if (entity.getClass() != entityMetaData.getEntityType()) {
            return false;
        }

        final Map<String, Object> thatValues = entityMetaData.extractValues(entity);

        return thatValues.keySet().stream()
                .anyMatch(fieldName -> !Objects.deepEquals(thatValues.get(fieldName), values.get(fieldName)));
    }
}
