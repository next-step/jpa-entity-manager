package persistence.sql.meta.simple;

import java.util.Objects;

public class SimpleValue {

    private final String value;

    public SimpleValue(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final SimpleValue that = (SimpleValue) object;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
