package persistence.sql.common.meta;

import jakarta.persistence.Column;
import java.lang.reflect.Field;

public class ColumnName {
    private String fieldName;
    private String name;

    private ColumnName(Field field) {
        this.fieldName = field.getName();
        extractName(field);
    }

    public static ColumnName of(Field field) {
        return new ColumnName(field);
    }

    /**
     * @Column의 name이 유효하다면 column명으로 설정하여 반환합니다.
     * 없을 경우 기존 field명을 반환합니다.
     */
    private void extractName(Field field) {
        String columnName = field.getName();

        if (field.isAnnotationPresent(Column.class)
            && !"".equals(field.getDeclaredAnnotation(Column.class).name())) {
            columnName = field.getDeclaredAnnotation(Column.class).name();
        }

        this.name = columnName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getName() {
        return name;
    }
}
