package persistence.sql.dml.query.clause;

import persistence.sql.dml.exception.IllegalFieldValueException;
import persistence.sql.dml.exception.NotFoundFieldNameException;
import persistence.sql.entity.model.DomainType;
import persistence.sql.entity.model.DomainTypes;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.constant.SqlConstant.COMMA;
import static persistence.sql.constant.SqlFormat.STRING_FORMAT;

public class ValueClause {
    private final List<String> values;

    private ValueClause(final List<String> values) {
        this.values = values;
    }

    public static ValueClause from(final Object instance,
                                   final DomainTypes domainTypes) {
        return new ValueClause(domainTypes.getDomainTypes()
                .stream()
                .map(domainType -> getValue(instance, getField(instance.getClass(), domainType.getName())))
                .collect(Collectors.toList()));
    }

    public List<String> getValues() {
        return values;
    }

    private static Field getField(final Class<?> clazz,
                                  final String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (Exception e) {
            throw new NotFoundFieldNameException();
        }
    }

    private static String getValue(final Object instance,
                                   final Field field) {
        try {
            field.setAccessible(true);
            return convertStringFormat(field.getType(), field.get(instance));
        } catch (Exception e) {
            throw new IllegalFieldValueException();
        }
    }

    private static String convertStringFormat(final Class<?> clazz,
                                              final Object value) {
        if (clazz == String.class) {
            return String.format(STRING_FORMAT.getFormat(), value);
        }
        if(value == null) {
            return null;
        }

        return value.toString();
    }

    public String toSql() {
        return String.join(COMMA.getValue(), values);
    }


}
