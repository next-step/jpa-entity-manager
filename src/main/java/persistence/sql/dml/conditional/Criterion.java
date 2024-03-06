package persistence.sql.dml.conditional;

import persistence.sql.entity.model.Operators;

import static persistence.sql.constant.SqlFormat.STRING_FORMAT;

public class Criterion {

    private final String key;
    private final String value;
    private final Operators operators;

    private Criterion(final String key,
                     final String value,
                     final Operators operators) {
        this.key = key;
        this.value = value;
        this.operators = operators;
    }

    public static Criterion of(final String key,
                               final String value) {
        return new Criterion(
                key,
                value,
                Operators.EQUALS
        );
    }

    public String toSql() {
        return new StringBuilder(key)
                .append(operators.getValue())
                .append(String.format(STRING_FORMAT.getFormat(), value))
                .toString();
    }
}
