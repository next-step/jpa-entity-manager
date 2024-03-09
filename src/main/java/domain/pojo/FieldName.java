package domain.pojo;

import jakarta.persistence.Column;

import java.lang.reflect.Field;

import static domain.utils.StringUtils.isBlankOrEmpty;

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
        FieldInfo fieldInfo = new FieldInfo(field);
        if (fieldInfo.isColumnField()) {
            return isBlankOrEmpty(field.getAnnotation(Column.class).name()) ? field.getName()
                    : field.getAnnotation(Column.class).name();
        }
        return field.getName();
    }
}
