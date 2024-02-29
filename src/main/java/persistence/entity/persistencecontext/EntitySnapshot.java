package persistence.entity.persistencecontext;

import java.util.Objects;
import java.util.stream.Collectors;
import static persistence.sql.constant.SqlConstant.COMMA;
import persistence.sql.meta.Table;

public class EntitySnapshot {

    private final Object hash;

    private EntitySnapshot(Object hash) {
        this.hash = hash;
    }

    public static EntitySnapshot from(Object entity) {
        Table table = Table.getInstance(entity.getClass());

        int hash = Objects.hash(entity.getClass(), table.getColumns().stream()
            .map(column -> column.getFieldValue(entity))
            .map(String::valueOf)
            .collect(Collectors.joining(COMMA.getValue())));

        return new EntitySnapshot(hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitySnapshot that = (EntitySnapshot) o;
        return Objects.equals(hash, that.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }
}
