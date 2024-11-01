package persistence.entity;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import persistence.sql.dml.ColumnValue;
import persistence.sql.dml.ColumnValues;

public class DirtyCheck {

    private final PersistenceContext persistenceContext;

    public DirtyCheck(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public boolean isDirty(Object entity) throws IllegalAccessException {
        Object snapshot = persistenceContext.getDatabaseSnapshot(entity);
        List<ColumnValue> previousColumnValues = new ColumnValues<>(snapshot).getValues();
        List<ColumnValue> currentColumnValues = new ColumnValues<>(entity).getValues();

        return findDirty(previousColumnValues, currentColumnValues);
    }

    private boolean findDirty(List<ColumnValue> previousColumnValues, List<ColumnValue> currentColumnValues) {
        if (previousColumnValues.size() != currentColumnValues.size()) {
            return true;
        }

        return IntStream.range(0, previousColumnValues.size())
            .anyMatch(i -> !isEqual(previousColumnValues.get(i), currentColumnValues.get(i)));
    }

    private boolean isEqual(ColumnValue columnValue, ColumnValue columnValue2) {
        return Objects.equals(columnValue.toStringValue(), columnValue2.toStringValue());
    }

}
