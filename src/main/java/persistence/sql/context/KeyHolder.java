package persistence.sql.context;

import java.io.Serializable;
import java.util.Objects;

public record KeyHolder(Class<?> entityType, Object key) implements Serializable {
    public KeyHolder {
        if (entityType == null || key == null) {
            throw new IllegalArgumentException("entityType and key must not be null");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyHolder keyHolder = (KeyHolder) o;
        return Objects.equals(key, keyHolder.key) && Objects.equals(entityType, keyHolder.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityType, key);
    }
}
