package persistence.entity.persistencecontext;

import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityColumns;
import persistence.sql.entitymetadata.model.EntityTable;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class EntitySnapShot {
    private final Map<EntityColumn, Object> snapShotColumns;

    private EntitySnapShot(Object entity, EntityColumns<Object> entityColumns) {
        this.snapShotColumns = new HashMap<>();

        for (EntityColumn<Object, ?> entityColumn : entityColumns) {
            snapShotColumns.put(entityColumn, entityColumn.getValue(entity));
        }
    }

    public <E> Set<EntityColumn<E, ?>> getDirtyColumns(E entity) {
        EntityTable<E> entityTable = (EntityTable<E>) new EntityTable<>(entity.getClass());

        Set<EntityColumn<E, ?>> dirtyColumns = new LinkedHashSet<>();

        for (EntityColumn<E, ?> column : entityTable.getColumns()) {
            if(!snapShotColumns.get(column).equals(column.getValue(entity))) {
                dirtyColumns.add(column);
            }
        }

        return dirtyColumns;
    }

    public static EntitySnapShot fromEntity(Object entity) {
        EntityTable entityTable = new EntityTable<>(entity.getClass());

        return new EntitySnapShot(entity, entityTable.getColumns());
    }
}
