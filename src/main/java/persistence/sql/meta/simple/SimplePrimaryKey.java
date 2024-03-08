package persistence.sql.meta.simple;

import jakarta.persistence.GenerationType;
import persistence.sql.meta.PrimaryKey;
import java.util.Objects;

public class SimplePrimaryKey implements PrimaryKey {

    private SimpleColumn primaryKey;

    public SimplePrimaryKey(final SimpleColumn primaryKey) {
        this.primaryKey = primaryKey;
    }

    @Override
    public String name() {
        return this.primaryKey.getFieldName();
    }

    @Override
    public Object value() {
        return this.primaryKey.value();
    }

    @Override
    public Class<?> type() {
        return this.primaryKey.type();
    }

    @Override
    public GenerationType strategy() {
        return this.primaryKey.generateType();
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final SimplePrimaryKey that = (SimplePrimaryKey) object;
        return Objects.equals(primaryKey, that.primaryKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primaryKey);
    }
}
