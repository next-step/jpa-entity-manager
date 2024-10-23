package orm;

import java.io.Serializable;
import java.util.Objects;

public class EntityKey implements Serializable {

    private final String tableName;
    private final Object idValue;

    public <E> EntityKey(TableEntity<E> entity) {
        this.tableName = entity.getTableName();
        this.idValue = entity.getIdValue();
    }

    public <E> EntityKey(Class<E> entity, Object id) {
        this.tableName = new TableEntity<>(entity).getTableName();
        this.idValue = id;
    }

    public Object getIdValue() {
        return idValue;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(tableName, entityKey.tableName) && Objects.equals(idValue, entityKey.idValue);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(tableName);
        result = 31 * result + Objects.hashCode(idValue);
        return result;
    }
}
