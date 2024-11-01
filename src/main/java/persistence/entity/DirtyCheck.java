package persistence.entity;

import persistence.sql.dml.ColumnValues;

public class DirtyCheck {

    private final PersistenceContext persistenceContext;

    public DirtyCheck(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public boolean isDirty(Object entity) throws IllegalAccessException {
        Object snapshot = persistenceContext.getDatabaseSnapshot(entity);
        return findDirty(new ColumnValues<>(snapshot), new ColumnValues<>(entity));
    }

    private boolean findDirty(ColumnValues previousColumnValues, ColumnValues currentColumnValues) {
        if (!previousColumnValues.hasSameSizeAs(currentColumnValues)) {
            return true;
        }

        return !previousColumnValues.areEqualTo(currentColumnValues);
    }
}
