package persistence.entity;

import persistence.sql.domain.ColumnOperation;
import persistence.sql.domain.DatabaseTable;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Snapshot {

    private final Map<String, Object> object;

    public Snapshot(Object entity) {
        DatabaseTable table = new DatabaseTable(entity);
        object = table.getAllColumns().stream().collect(Collectors.toMap(
                ColumnOperation::getJdbcColumnName,
                ColumnOperation::getColumnValue));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Snapshot)) return false;
        Snapshot snapshot = (Snapshot) o;
        return Objects.equals(object, snapshot.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object);
    }
}
