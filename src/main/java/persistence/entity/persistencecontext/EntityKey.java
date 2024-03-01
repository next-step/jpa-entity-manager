package persistence.entity.persistencecontext;

import java.util.Objects;
import persistence.sql.meta.Table;

public class EntityKey {

    private final Table table;
    private final Object id;

    private EntityKey(Table table, Object id) {
        this.table = table;
        this.id = id;
    }

    public static EntityKey from(Object entity) {
        Table table = Table.getInstance(entity.getClass());
        return new EntityKey(table, table.getIdValue(entity));
    }

    public static EntityKey of(Class<?> clazz, Object id) {
        Table table = Table.getInstance(clazz);
        return new EntityKey(table, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(table, entityKey.table) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, id);
    }
}
