package persistence.sql.dml.conditions;

import java.util.Objects;

public class WhereRecord {
    private final String name;
    private final String operator;
    private final Object value;

    private WhereRecord(String name, String operator, Object value) {
        this.name = name;
        this.operator = operator;
        this.value = value;
    }

    public static WhereRecord of(String name, String operator, Object value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(operator);
        Objects.requireNonNull(value);

        return new WhereRecord(name, operator, value);
    }

    public String getName() {
        return name;
    }

    public String getOperator() {
        return operator;
    }

    public Object getValue() {
        return value;
    }
}
