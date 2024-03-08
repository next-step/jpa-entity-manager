package persistence.entity;

import persistence.sql.mapping.Columns;

import java.util.Map;
import java.util.Objects;

public class Snapshot {
    private final Map<String, Object> values;
    public Snapshot(Object entity) {
        Columns columns = Columns.createColumnsWithValue(entity);
        values = columns.getValuesMap();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Snapshot snapshot = (Snapshot) o;
        return Objects.equals(values, snapshot.values);
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }
}
