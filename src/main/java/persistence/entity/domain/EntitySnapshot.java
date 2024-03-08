package persistence.entity.domain;

import persistence.sql.ddl.domain.Columns;
import persistence.sql.dml.domain.Value;
import persistence.sql.dml.domain.Values;

import java.util.Objects;
import java.util.stream.Collectors;

public class EntitySnapshot {

    private final int snapshot;

    public EntitySnapshot(Object entity) {
        Columns columns = new Columns(entity.getClass());
        Values values = new Values(columns, entity);

        this.snapshot = Objects.hash(entity.getClass(), values.getValues().stream()
                .map(Value::getValue)
                .collect(Collectors.joining()));
    }

    public int getSnapshot() {
        return snapshot;
    }

}
