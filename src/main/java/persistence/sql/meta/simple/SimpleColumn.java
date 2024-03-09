package persistence.sql.meta.simple;

import jakarta.persistence.GenerationType;
import persistence.sql.meta.Column;

import java.util.Objects;

public class SimpleColumn implements Column {

    private final String name;
    private final boolean isNullable;
    private final GenerationType generationType;
    private final Class<?> type;
    private Object value;


    public SimpleColumn(final String name, final boolean isNullable, final GenerationType generationType,
                        final Class<?> type) {
        this.name = name;
        this.isNullable = isNullable;
        this.generationType = generationType;
        this.type = type;
    }

    public SimpleColumn(final String name, final boolean isNullable, final GenerationType generationType,
                        final Class<?> type, final Object value) {
        this.name = name;
        this.isNullable = isNullable;
        this.generationType = generationType;
        this.type = type;
        this.value = value;
    }

    @Override
    public String getFieldName() {
        return this.name;
    }

    @Override
    public Object value() {
        return this.value;
    }

    @Override
    public Class<?> type()  {
        return this.type;
    }

    @Override
    public GenerationType generateType() {
        return this.generationType;
    }

    @Override
    public boolean isNullable() {
        return this.isNullable;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final SimpleColumn that = (SimpleColumn) object;
        return isNullable == that.isNullable && Objects.equals(name, that.name) && generationType == that.generationType && Objects.equals(type, that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, isNullable, generationType, type, value);
    }

    @Override
    public String toString() {
        return "SimpleColumn{" +
                "name='" + name + '\'' +
                ", isNullable=" + isNullable +
                ", generationType=" + generationType +
                ", type=" + type +
                ", value=" + value +
                '}';
    }
}
