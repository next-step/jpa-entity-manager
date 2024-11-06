package persistence.entity;

import java.util.List;
import persistence.sql.dml.ColumnValues;

public class DirtyCheck {

    private final PersistenceContext persistenceContext;

    public DirtyCheck(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public List<String> findDirtyColumns(Object entity) throws IllegalAccessException {
        Object snapshot = persistenceContext.getDatabaseSnapshot(entity);
        return new ColumnValues<>(snapshot).findDifferentColumns(new ColumnValues<>(entity));
    }

}
