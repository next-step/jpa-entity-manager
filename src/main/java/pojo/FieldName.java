package pojo;

import jakarta.persistence.Column;
import utils.StringUtils;

import java.lang.reflect.Field;

/**
 * 필드명
 */
public class FieldName {

    private final String name;

    public FieldName(Field field) {
        this.name = getFieldName(field);
    }

    public String getName() {
        return name;
    }

    private String getFieldName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            return StringUtils.isBlankOrEmpty(field.getAnnotation(Column.class).name()) ? field.getName()
                    : field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }
}
