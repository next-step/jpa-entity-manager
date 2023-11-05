package persistence.entity;

import persistence.sql.entitymetadata.model.EntityColumn;
import persistence.sql.entitymetadata.model.EntityColumns;
import persistence.sql.entitymetadata.model.EntityTable;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EntitySnapShot {
    private final Set<SnapShotColumn> snapShotColumns;

    private EntitySnapShot(Object entity, EntityColumns<Object> entityColumns) {
        this.snapShotColumns = entityColumns.stream()
                .map(entityColumn -> new SnapShotColumn(entityColumn, entityColumn.getValue(entity)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static EntitySnapShot fromEntity(Object entity) {
        EntityTable entityTable = new EntityTable<>(entity.getClass());

        return new EntitySnapShot(entity, entityTable.getColumns());
    }

    static class SnapShotColumn {
        private EntityColumn<?, ?> column;
        private Object value;

        public SnapShotColumn(EntityColumn<?, ?> column, Object value) {
            this.column = column;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SnapShotColumn that = (SnapShotColumn) o;
            return Objects.equals(column, that.column) && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(column, value);
        }
    }
}
