package persistence.sql.mapping;

import jakarta.persistence.GenerationType;
import persistence.sql.dialect.Dialect;

public class Column {

    private final String name;

    private final int type;

    private final Value value;

    private int length = 255;

    private boolean nullable = true;

    private boolean unique = false;

    private boolean pk = false;

    private GenerationType pkStrategy = null;

    public Column(final String columnName, final int sqlType, final Value value) {
        this.name = columnName;
        this.type = sqlType;
        this.value = value;
    }

    public Column(final String columnName, final int sqlType, final Value value, final int length, final boolean nullable, final boolean unique) {
        this.name = columnName;
        this.type = sqlType;
        this.value = value;
        this.length = length;
        this.nullable = nullable;
        this.unique = unique;
    }

    public Column clone() {
        final Column copy = new Column(this.name, this.type, this.getValue(), this.length, this.nullable, this.unique);
        copy.setPk(this.pk);
        copy.setStrategy(this.pkStrategy);

        return copy;
    }

    public String getName() {
        return name;
    }

    public String getSqlType(Dialect dialect) {
        return dialect.convertColumnType(this.type, this.getLength());
    }

    public Value getValue() {
        return value;
    }

    public int getLength() {
        return length;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isNotNull() {
        return !isNullable();
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isPk() {
        return pk;
    }

    public boolean isNotPk() {
        return !pk;
    }

    public boolean isIdentifierKey() {
        return this.pk && this.pkStrategy == GenerationType.IDENTITY;
    }

    public void setValue(final Object value) {
        this.value.setValue(value);
    }

    public void setPk(final boolean pk) {
        this.pk = pk;
    }

    public void setStrategy(final GenerationType strategy) {
        this.pkStrategy = strategy;
    }

}
