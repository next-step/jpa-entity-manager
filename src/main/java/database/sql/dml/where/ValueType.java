package database.sql.dml.where;

import database.sql.Util;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

enum ValueType {
    NULL(),
    LIST(),
    NUMBER(),
    STRING();

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9]+$");

    public static ValueType examineType(Object value) {
        if (value == null) return ValueType.NULL;
        if (value instanceof List) return ValueType.LIST;
        Matcher matcher = NUMBER_PATTERN.matcher(value.toString());
        if (matcher.matches()) return ValueType.NUMBER;
        return ValueType.STRING;
    }

    public String toQueryString(Object value) {
        switch (this) {
            case NULL:
                return "NULL";
            case LIST:
                return inClause((Collection<?>) value);
            case NUMBER:
                return value.toString();
            case STRING:
                return Util.quote(value);
        }
        return null;
    }

    private String inClause(Collection<?> value) {
        return value.stream()
                .map(Util::quote)
                .collect(Collectors.joining(", ", "(", ")"));
    }

}
