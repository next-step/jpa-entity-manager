package persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class EntityKey implements Serializable {

    private Long longTypeId;
    private String stringTypeId;

    public EntityKey() {}

    public EntityKey(Long longTypeId, String stringTypeId) {
        this.longTypeId = longTypeId;
        this.stringTypeId = stringTypeId;
    }

    // equals()와 hashCode()를 반드시 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey that = (EntityKey) o;
        return longTypeId.equals(that.longTypeId) && stringTypeId.equals(that.stringTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longTypeId, stringTypeId);
    }

    @Override
    public String toString() {
        return "EntityKey{longTypeId=" + longTypeId + ", stringTypeId='" + stringTypeId + "'}";
    }
}
