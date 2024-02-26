package persistence.sql.meta;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.lang.reflect.Field;

import static persistence.sql.meta.parser.ValueParser.valueParse;

public class EntityColumn {
    private final Field field;

    public EntityColumn(final Field field) {
        this.field = field;
    }

    public String getFieldName() {
        if (isNotBlankOf(field)) {
            return field.getAnnotation(Column.class).name();
        }

        return field.getName();
    }

    public String value(Object object) {
        return valueParse(this.field, object);
    }

    public Class<?> type()  {
        return this.field.getType();
    }

    public GenerationType generateType() {
        if (this.field.isAnnotationPresent(GeneratedValue.class)) {
            return this.field.getAnnotation(GeneratedValue.class).strategy();
        }

        return GenerationType.AUTO;
    }

    public boolean isNullable() {
        if (this.field.isAnnotationPresent(Column.class)) {
            return this.field.getAnnotation(Column.class).nullable();
        }

        return true;
    }

    private boolean isNotBlankOf(final Field field) {
        return field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isBlank();
    }
}
