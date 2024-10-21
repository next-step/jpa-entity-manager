package persistence.sql.context;

import java.util.Objects;

public record KeyHolder(Class<?> entityType, Object key) {

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
