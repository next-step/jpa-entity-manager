package persistence.entity;

import java.util.Objects;

public class SnapshotObject {
    private final String name;

    public SnapshotObject(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SnapshotObject that = (SnapshotObject) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
