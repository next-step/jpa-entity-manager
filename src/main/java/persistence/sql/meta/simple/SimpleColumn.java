package persistence.sql.meta.simple;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import persistence.sql.meta.Column;

import java.lang.reflect.Field;

import static persistence.sql.meta.parser.ValueParser.valueParse;

public class SimpleColumn implements Column {

    private final Field field;

    public SimpleColumn(final Field field) {
        this.field = field;
    }

    @Override
    public String getFieldName() {
        if (isNotBlankOf(field)) {
            return field.getAnnotation(jakarta.persistence.Column.class).name();
        }

        return field.getName();
    }

    @Override
    public String value(Object object) {
        return valueParse(this.field, object);
    }

    @Override
    public Class<?> type()  {
        return this.field.getType();
    }

    @Override
    public GenerationType generateType() {
        if (this.field.isAnnotationPresent(GeneratedValue.class)) {
            return this.field.getAnnotation(GeneratedValue.class).strategy();
        }

        return GenerationType.AUTO;
    }

    @Override
    public boolean isNullable() {
        if (this.field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            return this.field.getAnnotation(jakarta.persistence.Column.class).nullable();
        }

        return true;
    }

    private boolean isNotBlankOf(final Field field) {
        return field.isAnnotationPresent(jakarta.persistence.Column.class) && !field.getAnnotation(jakarta.persistence.Column.class).name().isBlank();
    }
}
