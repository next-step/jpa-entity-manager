package persistence.entity;

import static persistence.util.ReflectionCopy.copy;

import java.util.List;
import lombok.Getter;
import persistence.sql.dml.ColumnValues;

public class EntityEntry {
    @Getter
    private final Object entity;
    @Getter
    private Status status;
    private Object snapshot;

    public EntityEntry(Object entity, Status status) {
        this.entity = entity;
        this.status = status;
    }

    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }

    public void updateSnapshot(Object entity) {
        this.snapshot = copy(entity);
    }

    public List<String> findDirtyColumns(Object entity) throws IllegalAccessException {
        if (status != Status.MANAGED) {
            throw new IllegalAccessException("Entity is not managed");
        }
        return new ColumnValues<>(snapshot).findDifferentColumns(new ColumnValues<>(entity));
    }

}
